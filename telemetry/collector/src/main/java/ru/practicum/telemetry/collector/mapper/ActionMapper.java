package ru.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.telemetry.collector.model.hub.ScenarioAction;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@UtilityClass
public class ActionMapper {
    public DeviceActionAvro toAvro(ScenarioAction action) {
        if (action == null) throw new IllegalArgumentException("ScenarioAction cannot be null");

        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }
}
