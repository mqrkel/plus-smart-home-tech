package ru.yandex.practicum.analyzer.infrastructure.processors;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
public abstract class HubEventProcessorBase implements HubEventProcessor {

    @Override
    public final void handle(HubEventAvro event) {
        log.debug("Handle hub event {}", event.getPayload().getClass().getSimpleName());
        try {
            validatePayload(event.getPayload());
            handleImpl(event);
        } catch (Exception e) {
            log.error("{} ({}): {}", e.getClass(), e.getCause(), e.getMessage());
        }
    }

    protected abstract void handleImpl(HubEventAvro event);

    private void validatePayload(Object payload) {
        if (!getPayloadClass().isInstance(payload))
            throw new IllegalArgumentException("Expected " + getPayloadClass() + " but got " + payload.getClass());
    }
}
