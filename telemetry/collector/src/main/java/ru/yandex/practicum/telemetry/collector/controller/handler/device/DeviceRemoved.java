package ru.yandex.practicum.telemetry.collector.controller.handler.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ProtoMapper;
import ru.yandex.practicum.telemetry.collector.service.CollectorService;

@Component
@RequiredArgsConstructor
public class DeviceRemoved implements DeviceEvent {
    private final ProtoMapper mapper;
    private final CollectorService service;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro avro = mapper.toAvro(event);
        service.sendHubData(avro);
    }
}
