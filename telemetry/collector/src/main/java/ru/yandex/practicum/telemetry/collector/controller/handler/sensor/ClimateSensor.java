package ru.yandex.practicum.telemetry.collector.controller.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ProtoMapper;
import ru.yandex.practicum.telemetry.collector.service.CollectorService;

@Component
@RequiredArgsConstructor
public class ClimateSensor implements SensorEvent {
    private final ProtoMapper mapper;
    private final CollectorService service;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro avro = mapper.toAvro(event);
        service.sendSensorData(avro);
    }
}
