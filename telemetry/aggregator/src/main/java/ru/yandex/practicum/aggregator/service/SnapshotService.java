package ru.yandex.practicum.aggregator.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SnapshotService {
    void handle(ConsumerRecord<String, SensorEventAvro> record);
}
