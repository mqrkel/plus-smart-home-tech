package ru.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public class DeviceTypeMapper {
    public static DeviceTypeAvro toAvro(DeviceTypeProto type) {
        String typeWithoutPrefix = type.name().replace("DEVICE_", "");
        return DeviceTypeAvro.valueOf(typeWithoutPrefix);
    }
}
