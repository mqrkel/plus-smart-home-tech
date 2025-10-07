package ru.yandex.practicum.telemetry.collector.controller.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEvent {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
