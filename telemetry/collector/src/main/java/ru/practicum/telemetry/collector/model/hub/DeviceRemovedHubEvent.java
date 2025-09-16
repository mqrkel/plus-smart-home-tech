package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.telemetry.collector.model.enums.HubEventType;

@Getter
@Setter
@ToString
public class DeviceRemovedHubEvent extends HubEvent {

    @NotNull
    private String deviceId;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
