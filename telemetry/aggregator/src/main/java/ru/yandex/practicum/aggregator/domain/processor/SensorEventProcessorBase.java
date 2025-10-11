package ru.yandex.practicum.aggregator.domain.processor;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public abstract class SensorEventProcessorBase implements SensorEventProcessor {

    @Override
    public boolean updateIfChanged(SensorStateAvro state, SensorEventAvro event) {
        if (state.getTimestamp().isAfter(event.getTimestamp()))
            return false;

        if (Objects.equals(state.getPayload(), event.getPayload()))
            return false;

        return this.updatePayloadIfChanged(state.getPayload(), event.getPayload());
    }

    protected abstract boolean updatePayloadIfChanged(Object payload, Object payload1);

    protected  <T> T castPayload(Object payload, Class<T> targetClass) {
        if (targetClass.isInstance(payload)) {
            return targetClass.cast(payload);
        }
        throw new IllegalArgumentException("Expected " + targetClass + " but got " + payload.getClass());
    }

    protected <T> boolean updateFieldIfChanged(Supplier<T> getter, Consumer<T> setter, T newValue) {
        boolean changed = !Objects.equals(getter.get(), newValue);
        if (changed) {
            log.debug("Changed sensor value {} -> {}", getter.get(), newValue);
            setter.accept(newValue);
        }
        return changed;
    }
}
