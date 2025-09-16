package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ru.practicum.telemetry.collector.model.enums.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString
public class ScenarioAddedHubEvent extends HubEvent {

    @NotNull
    @Length(min = 3)
    private String name;

    @NotNull
    @NotEmpty
    private List<ScenarioCondition> conditions;

    @NotNull
    @NotEmpty
    private List<ScenarioAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
