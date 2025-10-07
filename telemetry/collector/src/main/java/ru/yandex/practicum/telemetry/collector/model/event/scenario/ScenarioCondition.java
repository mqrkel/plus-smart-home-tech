package ru.yandex.practicum.telemetry.collector.model.event.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.event.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.collector.model.event.enums.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}
