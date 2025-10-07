package ru.yandex.practicum.telemetry.collector.model.event.device;

import ru.yandex.practicum.telemetry.collector.model.event.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.event.HubEvent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
