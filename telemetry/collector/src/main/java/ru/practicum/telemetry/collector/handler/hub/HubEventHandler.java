package ru.practicum.telemetry.collector.handler.hub;

import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.enums.HubEventType;

public interface HubEventHandler {
    HubEventType getEventType();
    void handleEvent(HubEvent e);
}
