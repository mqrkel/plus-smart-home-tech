package ru.yandex.practicum.telemetry.collector.model.event.scenario;

import ru.yandex.practicum.telemetry.collector.model.event.device.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.event.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.event.HubEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    private String name;

    @NotNull
    List<ScenarioCondition> conditions;

    @NotNull
    List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
