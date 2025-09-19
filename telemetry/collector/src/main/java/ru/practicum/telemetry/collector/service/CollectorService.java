package ru.practicum.telemetry.collector.service;

import jakarta.validation.Valid;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;

public interface CollectorService {

    void collectSensorEvent(SensorEvent event);

    void collectHubEvent(@Valid HubEvent event);
}
