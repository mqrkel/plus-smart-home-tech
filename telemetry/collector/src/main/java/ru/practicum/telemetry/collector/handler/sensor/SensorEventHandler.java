package ru.practicum.telemetry.collector.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getEventType();
    void handleEvent(SensorEventProto event);
}
