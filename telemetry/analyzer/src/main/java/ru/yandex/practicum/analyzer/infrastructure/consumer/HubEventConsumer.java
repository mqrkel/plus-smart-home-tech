package ru.yandex.practicum.analyzer.infrastructure.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.config.KafkaConfig;
import ru.yandex.practicum.deserializer.HubEventAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class HubEventConsumer extends BaseConsumer<HubEventAvro> {

    @Autowired
    public HubEventConsumer(KafkaConfig kafkaConfig) {
        super(kafkaConfig.getHubEventsConsumer());
    }

    @Override
    protected String getDeserializerName() {
        return HubEventAvroDeserializer.class.getCanonicalName();
    }
}
