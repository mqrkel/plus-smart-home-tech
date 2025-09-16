package ru.practicum.telemetry.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.model.hub.DeviceRemovedHubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.enums.HubEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    protected DeviceRemovedEventAvro convertToAvro(HubEvent event) {
        ensureCorrectEventType(event, DeviceRemovedHubEvent.class);
        DeviceRemovedHubEvent hubEvent = (DeviceRemovedHubEvent)event;
        return DeviceRemovedEventAvro.newBuilder()
                .setId(hubEvent.getDeviceId())
                .build();
    }
}
