package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.telemetry.collector.model.enums.HubEventType;

@Getter
@Setter
@ToString
public class ScenarioRemovedHubEvent extends HubEvent {

    @NotBlank
    @Length(min = 3)
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
