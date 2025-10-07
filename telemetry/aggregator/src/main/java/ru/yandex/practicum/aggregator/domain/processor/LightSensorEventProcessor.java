package ru.yandex.practicum.aggregator.domain.processor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventProcessor extends SensorEventProcessorBase {

    @Override
    public Class<?> getPayloadClass() {
        return LightSensorAvro.class;
    }

    @Override
    protected boolean updatePayloadIfChanged(Object state, Object event) {
        LightSensorAvro curPayload = castPayload(state, LightSensorAvro.class);
        LightSensorAvro newPayload = castPayload(event, LightSensorAvro.class);

        boolean changed = false;
        changed |= updateFieldIfChanged(curPayload::getLinkQuality, curPayload::setLinkQuality, newPayload.getLinkQuality());
        changed |= updateFieldIfChanged(curPayload::getLuminosity, curPayload::setLuminosity, newPayload.getLuminosity());

        return changed;
    }
}
