package ru.yandex.practicum.aggregator.domain.processor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventProcessor extends SensorEventProcessorBase {

    @Override
    public Class<?> getPayloadClass() {
        return TemperatureSensorAvro.class;
    }

    @Override
    protected boolean updatePayloadIfChanged(Object state, Object event) {
        TemperatureSensorAvro curPayload = castPayload(state, TemperatureSensorAvro.class);
        TemperatureSensorAvro newPayload = castPayload(event, TemperatureSensorAvro.class);

        boolean changed = false;
        changed |= updateFieldIfChanged(curPayload::getTemperatureC, curPayload::setTemperatureC, newPayload.getTemperatureC());
        changed |= updateFieldIfChanged(curPayload::getTemperatureF, curPayload::setTemperatureF, newPayload.getTemperatureF());

        return changed;
    }
}
