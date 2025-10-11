package ru.practicum.telemetry.collector.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface CollectorService {

    void collectSensorEvent(SensorEventProto request);

    void collectHubEvent(HubEventProto request);
}
