package ru.yandex.practicum.aggregator.domain.processor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventProcessor extends SensorEventProcessorBase {

    @Override
    public Class<?> getPayloadClass() {
        return MotionSensorAvro.class;
    }

    @Override
    protected boolean updatePayloadIfChanged(Object state, Object event) {
        MotionSensorAvro curPayload = castPayload(state, MotionSensorAvro.class);
        MotionSensorAvro newPayload = castPayload(event, MotionSensorAvro.class);

        boolean changed = false;
        changed |= updateFieldIfChanged(curPayload::getMotion, curPayload::setMotion, newPayload.getMotion());
        changed |= updateFieldIfChanged(curPayload::getVoltage, curPayload::setVoltage, newPayload.getVoltage());
        changed |= updateFieldIfChanged(curPayload::getLinkQuality, curPayload::setLinkQuality, newPayload.getLinkQuality());
        return changed;
    }
}
