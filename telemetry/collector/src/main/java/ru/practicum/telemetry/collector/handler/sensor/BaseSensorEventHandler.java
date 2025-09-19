package ru.practicum.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T> implements SensorEventHandler {

    @Value("${kafka.topics.sensor}")
    private String topic;

    private final KafkaEventProducer producer;

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handleEvent(SensorEvent event) {

        T sensorEventAvro = mapToAvro(event);
        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(sensorEventAvro)
                .build();

        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topic,
                null,
                Instant.now().toEpochMilli(),
                eventAvro.getHubId(),
                eventAvro);
        log.debug("Sending event to kafka: {}", producerRecord);
        producer.send(producerRecord);
    }

    public void validateEventType(SensorEvent event, Class<? extends SensorEvent> expectedClass) {
        if (!expectedClass.isInstance(event)) {
            String message = "Expected " + expectedClass.getSimpleName()
                    + " but got " + event.getClass().getSimpleName() + ". Event ID: " + event.getId();
            throw new IllegalArgumentException(message);
        }
    }
}
