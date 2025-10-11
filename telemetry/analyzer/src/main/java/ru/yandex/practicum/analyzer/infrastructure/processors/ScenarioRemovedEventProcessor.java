package ru.yandex.practicum.analyzer.infrastructure.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.domain.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventProcessor extends HubEventProcessorBase {

    private final ScenarioRepository scenarioRepository;

    @Override
    public Class<?> getPayloadClass() {
        return ScenarioRemovedEventAvro.class;
    }

    @Override
    @Transactional
    protected void handleImpl(HubEventAvro event) {
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();
        scenarioRepository.deleteByHubIdAndName(event.getHubId(), payload.getName());
        log.debug("Scenario {} was successfully removed", payload.getName());
    }
}
