package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.telemetry.collector.model.enums.ConditionOperator;
import ru.practicum.telemetry.collector.model.enums.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    @NotBlank
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private ConditionOperator operation;

    @NotNull
    private Integer value;
}
