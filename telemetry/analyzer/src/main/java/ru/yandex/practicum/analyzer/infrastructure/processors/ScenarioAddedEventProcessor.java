package ru.yandex.practicum.analyzer.infrastructure.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.domain.model.*;
import ru.yandex.practicum.analyzer.exception.DeviceNotFoundException;
import ru.yandex.practicum.analyzer.exception.DuplicateScenarioException;
import ru.yandex.practicum.analyzer.domain.repository.ActionRepository;
import ru.yandex.practicum.analyzer.domain.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.domain.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.domain.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventProcessor extends HubEventProcessorBase {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public Class<?> getPayloadClass() {
        return ScenarioAddedEventAvro.class;
    }

    @Override
    @Transactional
    protected void handleImpl(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();

        validateDevices(event.getHubId(), payload.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toSet()));

        validateDevices(event.getHubId(), payload.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toSet()));

        Scenario scenario = saveScenario(event.getHubId(), payload.getName());
        List<ScenarioCondition> scenarioConditions = saveConditions(scenario, payload.getConditions());
        List<ScenarioAction> scenarioActions = saveActions(scenario, payload.getActions());
        scenario.setScenarioConditions(scenarioConditions);
        scenario.setScenarioActions(scenarioActions);
        log.debug("Scenario {} was successfully added", scenario.getName());
    }

    private void validateDevices(String hubId, Set<String> deviceIds) {

        if (deviceIds.isEmpty())
            return;

        List<String> foundIds = sensorRepository.findAllByHubIdAndIdIn(hubId, deviceIds)
                .stream()
                .map(Sensor::getId)
                .toList();

        if (foundIds.size() != deviceIds.size()) {
            Set<String> badIds = deviceIds.stream()
                    .filter(id -> !deviceIds.contains(id))
                    .collect(Collectors.toSet());
            throw new DeviceNotFoundException(badIds);
        }
    }

    private List<ScenarioCondition> saveConditions(Scenario scenario, List<ScenarioConditionAvro> conditionAvros) {
        log.debug("saving of conditions ({})", conditionAvros.size());
        List<ScenarioCondition> scenarioConditions = new ArrayList<>();

        Set<String> sensorIds = conditionAvros.stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toSet());

        Map<String, Sensor> sensorsMap = findAllDevices(sensorIds);

        for (ScenarioConditionAvro conditionAvro : conditionAvros) {

            Condition condition = conditionRepository.save(Condition.builder()
                    .type(conditionAvro.getType())
                    .value(conditionAvro.getValue())
                    .operation(conditionAvro.getOperation())
                    .build());

            Sensor sensor = sensorsMap.get(conditionAvro.getSensorId());
            if (sensor == null) {
                throw new DeviceNotFoundException("Sensor " + conditionAvro.getSensorId() + " not found in cached map");
            }

            scenarioConditions.add(ScenarioCondition.builder()
                .scenario(scenario)
                .sensor(sensor)
                .condition(condition)
                .build());
        }
        return scenarioConditions;
    }

    private List<ScenarioAction> saveActions(Scenario scenario, List<DeviceActionAvro> actionAvros) {
        log.debug("saving of actions ({})", actionAvros.size());
        List<ScenarioAction> scenarioActions = new ArrayList<>();

        Set<String> sensorIds = actionAvros.stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toSet());

         Map<String, Sensor> sensorsMap = findAllDevices(sensorIds);

        for (DeviceActionAvro actionAvro : actionAvros) {

            Action action = actionRepository.save(Action.builder()
                    .type(actionAvro.getType())
                    .value(actionAvro.getValue())
                    .build());

            Sensor sensor = sensorsMap.get(actionAvro.getSensorId());
            if (sensor == null) {
                throw new DeviceNotFoundException("Sensor " + actionAvro.getSensorId() + " not found in cached map");
            }

            scenarioActions.add(ScenarioAction.builder()
                    .sensor(sensor)
                    .action(action)
                    .scenario(scenario)
                    .build());
        }
        return scenarioActions;
    }

    private Map<String, Sensor> findAllDevices(Set<String> sensorIds) {
        return sensorRepository.findAllById(sensorIds)
                .stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));
    }

    private Scenario saveScenario(String hubId, String name) {
        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);
        if (existing.isPresent())
            throw new DuplicateScenarioException(name, hubId);

        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(name)
                .build();
        log.debug("saving of scenario {}", scenario);
        return scenarioRepository.save(scenario);
    }
}
