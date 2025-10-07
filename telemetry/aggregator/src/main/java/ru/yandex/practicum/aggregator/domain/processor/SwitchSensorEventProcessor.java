package ru.yandex.practicum.aggregator.domain.processor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventProcessor extends SensorEventProcessorBase {

    @Override
    public Class<?> getPayloadClass() {
        return SwitchSensorAvro.class;
    }

    @Override
    protected boolean updatePayloadIfChanged(Object state, Object event) {
        SwitchSensorAvro curPayload = castPayload(state, SwitchSensorAvro.class);
        SwitchSensorAvro newPayload = castPayload(event, SwitchSensorAvro.class);

        return updateFieldIfChanged(curPayload::getState, curPayload::setState, newPayload.getState());
    }
}
