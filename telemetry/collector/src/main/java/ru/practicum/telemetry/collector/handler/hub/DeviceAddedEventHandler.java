package ru.practicum.telemetry.collector.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.mapper.DeviceTypeMapper;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Slf4j
@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getEventType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro toAvro(HubEventProto event) {
        validateEventType(event);
        DeviceAddedEventProto hubEvent = event.getDeviceAdded();

        DeviceTypeAvro deviceTypeAvro = DeviceTypeMapper.toAvro(hubEvent.getType());

        return DeviceAddedEventAvro.newBuilder()
                .setId(hubEvent.getId())
                .setDeviceType(deviceTypeAvro)
                .build();
    }
}
