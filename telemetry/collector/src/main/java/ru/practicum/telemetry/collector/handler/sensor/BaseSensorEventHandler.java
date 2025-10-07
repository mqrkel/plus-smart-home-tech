package ru.practicum.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T> implements SensorEventHandler {

    @Value("${kafka.topics.sensor}")
    private String topic;

    private final KafkaEventProducer producer;

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handleEvent(SensorEventProto event) {

        T sensorEventAvro = mapToAvro(event);
        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
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

    public void validateEventType(SensorEventProto event) {
        if (event.getPayloadCase() != getEventType()) {
            String message = "Expected " + getEventType()
                    + " but got " + event.getPayloadCase() + ". Hub ID: " + event.getHubId();
            throw new IllegalArgumentException(message);
        }
    }
}
