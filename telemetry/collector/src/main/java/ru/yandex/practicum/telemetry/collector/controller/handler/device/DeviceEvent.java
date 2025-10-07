package ru.yandex.practicum.telemetry.collector.controller.handler.device;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface DeviceEvent {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
