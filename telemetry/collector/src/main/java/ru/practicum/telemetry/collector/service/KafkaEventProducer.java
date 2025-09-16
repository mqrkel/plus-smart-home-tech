package ru.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaProducer<String, SpecificRecordBase> producer;

    public void send(ProducerRecord<String, SpecificRecordBase> message) {
        producer.send(message, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправки в топик: {}", metadata.topic(), exception);
            } else {
                log.debug("Успешно отправлено в топик: {}", metadata.topic());
            }
        });
    }
}
