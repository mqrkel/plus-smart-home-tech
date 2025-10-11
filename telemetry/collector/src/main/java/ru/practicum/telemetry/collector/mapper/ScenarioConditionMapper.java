package ru.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@UtilityClass
public class ScenarioConditionMapper {

    public ScenarioConditionAvro toAvro(ScenarioConditionProto sc) {
        ConditionTypeAvro typeAvro = ConditionTypeAvro.valueOf(sc.getType().name());
        ConditionOperationAvro operationAvro = ConditionOperationAvro.valueOf(sc.getOperation().name());
        Integer value = sc.getValueCase() == ScenarioConditionProto.ValueCase.INT_VALUE
                ? sc.getIntValue()
                : (sc.getBoolValue() ? 1 : 0);

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(sc.getSensorId())
                .setType(typeAvro)
                .setOperation(operationAvro)
                .setValue(value)
                .build();
    }
}
