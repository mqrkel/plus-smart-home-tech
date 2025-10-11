package ru.yandex.practicum.analyzer.infrastructure.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.domain.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventProcessor extends HubEventProcessorBase {

    private final SensorRepository sensorRepository;

    @Override
    public Class<?> getPayloadClass() {
        return DeviceRemovedEventAvro.class;
    }

    @Override
    @Transactional
    protected void handleImpl(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        sensorRepository.deleteByHubIdAndId(event.getHubId(), payload.getId());
        sensorRepository.flush();
        log.debug("Device {} was successfully removed", payload.getId());
    }
}
