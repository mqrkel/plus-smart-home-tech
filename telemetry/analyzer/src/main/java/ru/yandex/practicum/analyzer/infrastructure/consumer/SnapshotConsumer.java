package ru.yandex.practicum.analyzer.infrastructure.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaConfig;
import ru.yandex.practicum.deserializer.SensorsSnapshotAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
public class SnapshotConsumer extends BaseConsumer<SensorsSnapshotAvro> {

    @Autowired
    public SnapshotConsumer(KafkaConfig kafkaConfig) {
        super(kafkaConfig.getSnapshotsConsumer());
    }

    @Override
    protected String getDeserializerName() {
        return SensorsSnapshotAvroDeserializer.class.getCanonicalName();
    }
}
