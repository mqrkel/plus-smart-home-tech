package ru.practicum.telemetry.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    @Value("${kafka.topics.hub}")
    private String topic;

    private final KafkaEventProducer producer;

    @Override
    public void handleEvent(HubEventProto event) {
        log.debug("handleEvent");
        T payload = toAvro(event);
        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
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

    protected abstract T toAvro(HubEventProto event);

    public void validateEventType(HubEventProto event) {
        if (event.getPayloadCase() != getEventType()) {
            String message = "Expected " + getEventType()
                    + " but got " + event.getPayloadCase() + ". Hub ID: " + event.getHubId();
            throw new IllegalArgumentException(message);
        }
    }
}
