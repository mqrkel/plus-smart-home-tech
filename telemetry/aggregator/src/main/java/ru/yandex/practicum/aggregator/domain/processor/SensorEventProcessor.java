package ru.yandex.practicum.aggregator.domain.processor;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

public interface SensorEventProcessor {

    Class<?> getPayloadClass();

    boolean updateIfChanged(SensorStateAvro state, SensorEventAvro event);
}
