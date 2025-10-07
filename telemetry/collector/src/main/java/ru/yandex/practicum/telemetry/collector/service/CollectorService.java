package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface CollectorService {
    void sendSensorData(SensorEventAvro event);

    void sendHubData(HubEventAvro event);
}
