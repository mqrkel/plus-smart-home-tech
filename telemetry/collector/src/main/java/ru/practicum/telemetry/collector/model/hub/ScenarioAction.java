package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.telemetry.collector.model.enums.DeviceActionType;

@Getter
@Setter
@ToString
public class ScenarioAction {

    @NotBlank
    private String sensorId;

    @NotNull
    private DeviceActionType type;

    private Integer value;
}
