package ru.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@UtilityClass
public class ScenarioActionMapper {

    public DeviceActionAvro toAvro(DeviceActionProto action) {
        ActionTypeAvro actionType = toAvro(action.getType());
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(actionType)
                .setValue(action.getI())
                .build();
    }

    private static ActionTypeAvro toAvro(ActionTypeProto type) {
        return ActionTypeAvro.valueOf(type.name().replace("ACTION_", ""));
    }
}
