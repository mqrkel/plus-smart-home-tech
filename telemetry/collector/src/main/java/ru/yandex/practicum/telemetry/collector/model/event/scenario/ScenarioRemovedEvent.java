package ru.yandex.practicum.telemetry.collector.model.event.scenario;

import ru.yandex.practicum.telemetry.collector.model.event.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.event.HubEvent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
