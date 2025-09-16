package ru.practicum.telemetry.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.telemetry.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@UtilityClass
public class ConditionMapper {
    public ScenarioConditionAvro toAvro(ScenarioCondition sc) {
        if (sc == null) throw new IllegalArgumentException("ScenarioCondition cannot be null");

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(sc.getSensorId())
                .setType(ConditionTypeAvro.valueOf(sc.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(sc.getOperation().name()))
                .setValue(sc.getValue())
                .build();
    }
}
