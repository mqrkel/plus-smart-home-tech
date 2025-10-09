package ru.yandex.practicum.analyzer.infrastructure.processors;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventProcessor {

    Class<?> getPayloadClass();

    void handle(HubEventAvro event);
}
