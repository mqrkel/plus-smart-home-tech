package ru.practicum.telemetry.collector.handler.sensor;

import ru.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.practicum.telemetry.collector.model.enums.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getEventType();
    void handleEvent(SensorEvent e);
}
