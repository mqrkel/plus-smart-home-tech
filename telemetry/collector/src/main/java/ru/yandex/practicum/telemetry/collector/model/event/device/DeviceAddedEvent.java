package ru.yandex.practicum.telemetry.collector.model.event.device;

import ru.yandex.practicum.telemetry.collector.model.event.enums.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.event.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.event.HubEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
