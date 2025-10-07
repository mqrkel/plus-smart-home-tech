package ru.yandex.practicum.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventAvroDeserializer extends GeneralAvroDeserializer<SensorEventAvro> {

    public SensorEventAvroDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
