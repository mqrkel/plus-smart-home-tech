package ru.yandex.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaConfig;
import ru.yandex.practicum.aggregator.kafka.AggregatorKafkaClient;
import ru.yandex.practicum.aggregator.domain.processor.SensorEventProcessor;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SnapshotServiceImpl implements SnapshotService {

    private final Map<String, SensorsSnapshotAvro> sensorsSnapshots = new HashMap<>();
    private final AggregatorKafkaClient kafkaClient;
    private final KafkaConfig kafkaConfig;

    private final Map<Class<?>, SensorEventProcessor> processors;

    public SnapshotServiceImpl(KafkaConfig kafkaConfig,
                               AggregatorKafkaClient kafkaClient,
                               List<SensorEventProcessor> processors) {
        this.kafkaConfig = kafkaConfig;
        this.kafkaClient = kafkaClient;
        this.processors = processors.stream()
                .collect(Collectors.toMap(SensorEventProcessor::getPayloadClass, Function.identity()));
    }

    @Override
    public void handle(ConsumerRecord<String, SensorEventAvro> record) {
        try {
            SensorEventAvro event = record.value();
            String hubId = event.getHubId();
            SensorsSnapshotAvro snapshot = sensorsSnapshots.computeIfAbsent(hubId, this::createNewSnapshot);
            boolean updated = updateSnapshot(snapshot, event);

            if (updated) {
                snapshot.setTimestamp(event.getTimestamp());
                log.debug("↑ Updated snapshot: {}", snapshot);
                sendSnapshot(hubId, snapshot);
            } else {
                log.debug("- Skipped snapshot (no changes): {}", snapshot);
            }
        } catch (Exception ex) {
            log.error("Failed to process record: {}", record);
            log.error(ex.getMessage());
        }
    }

    private boolean updateSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
        String sensorId = event.getId();
        SensorEventProcessor sensorEventProcessor = getProcessorOrThrow(event);

        boolean isNewSensor = !snapshot.getSensorsState().containsKey(sensorId);
        SensorStateAvro sensorState = snapshot.getSensorsState().computeIfAbsent(sensorId,
                k -> createNewSensorState(event));

        boolean isDataChanged = sensorEventProcessor.updateIfChanged(sensorState, event);
        return isNewSensor || isDataChanged;
    }

    private SensorsSnapshotAvro createNewSnapshot(String hubId) {
        log.debug("create snapshot for {}", hubId);
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(Instant.EPOCH)
                .setSensorsState(new HashMap<>())
                .build();
    }

    private SensorStateAvro createNewSensorState(SensorEventAvro eventAvro) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(eventAvro.getTimestamp())
                .setPayload(eventAvro.getPayload())
                .build();
    }

    private SensorEventProcessor getProcessorOrThrow(SensorEventAvro event) {
        Class<?> aClass = event.getPayload().getClass();
        SensorEventProcessor processor = processors.get(aClass);
        if (processor == null) {
            throw new IllegalArgumentException("No processor found for payload type: " + aClass.getSimpleName());
        }
        return processor;
    }

    private void sendSnapshot(String hubId, SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                kafkaConfig.getProducer().getTopic(),
                null,
                Instant.now().toEpochMilli(),
                hubId,
                snapshot);
        kafkaClient.getProducer().send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send message to topic: {}", metadata.topic(), exception);
            } else {
                log.debug("Successfully sent to topic: {}", metadata.topic());
            }
        });
    }
}
