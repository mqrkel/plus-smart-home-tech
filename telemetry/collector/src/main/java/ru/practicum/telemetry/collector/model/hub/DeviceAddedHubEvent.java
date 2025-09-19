package ru.practicum.telemetry.collector.model.hub;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.telemetry.collector.model.enums.DeviceType;
import ru.practicum.telemetry.collector.model.enums.HubEventType;

@Getter
@Setter
@ToString
public class DeviceAddedHubEvent extends HubEvent {

    @NotNull
    @JsonProperty("id")
    private String deviceId;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
