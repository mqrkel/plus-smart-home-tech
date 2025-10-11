package ru.yandex.practicum.analyzer.infrastructure.mapper;

import com.google.protobuf.util.Timestamps;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.analyzer.domain.model.Scenario;
import ru.yandex.practicum.analyzer.domain.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;

@UtilityClass
public class DeviceActionMapper {

    public DeviceActionRequest toActionRequest(Scenario scenario, ScenarioAction action) {
        return DeviceActionRequest.newBuilder()
                .setTimestamp(Timestamps.now())
                .setScenarioName(scenario.getName())
                .setHubId(scenario.getHubId())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(ActionTypeProto.valueOf("ACTION_" + action.getAction().getType().name()))
                        .setI(action.getAction().getValue())
                        .build())
                .build();
    }
}
