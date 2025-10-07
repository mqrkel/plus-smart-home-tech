package ru.yandex.practicum.telemetry.collector.model.event.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.event.enums.ActionType;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
