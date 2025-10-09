package ru.yandex.practicum.analyzer.infrastructure.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.exception.DuplicateDeviceException;
import ru.yandex.practicum.analyzer.domain.model.Sensor;
import ru.yandex.practicum.analyzer.domain.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventProcessor extends HubEventProcessorBase {

    private final SensorRepository sensorRepository;

    @Override
    public Class<?> getPayloadClass() {
        return DeviceAddedEventAvro.class;
    }

    @Override
    @Transactional
    protected void handleImpl(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();

        Optional<Sensor> existing = sensorRepository.findById(payload.getId());
        if (existing.isPresent()) {
            throw new DuplicateDeviceException(payload.getId(), event.getHubId());
        }

        Sensor sensor = Sensor.builder()
                .id(payload.getId())
                .hubId(event.getHubId())
                .build();
        sensorRepository.save(sensor);
        log.debug("Device {} ({}) was successfully added", payload.getId(), payload.getDeviceType());
    }
}
