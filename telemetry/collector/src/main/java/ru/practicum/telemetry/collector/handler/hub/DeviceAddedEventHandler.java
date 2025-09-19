package ru.practicum.telemetry.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.model.hub.DeviceAddedHubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.enums.HubEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro convertToAvro(HubEvent event) {
        ensureCorrectEventType(event, DeviceAddedHubEvent.class);
        DeviceAddedHubEvent hubEvent = (DeviceAddedHubEvent)event;

        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name());
        return DeviceAddedEventAvro.newBuilder()
                .setId(hubEvent.getDeviceId())
                .setDeviceType(deviceTypeAvro)
                .build();
    }
}
