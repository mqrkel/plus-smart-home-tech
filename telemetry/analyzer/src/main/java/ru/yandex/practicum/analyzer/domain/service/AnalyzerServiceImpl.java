package ru.yandex.practicum.analyzer.domain.service;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.infrastructure.mapper.DeviceActionMapper;
import ru.yandex.practicum.analyzer.domain.model.Scenario;
import ru.yandex.practicum.analyzer.domain.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.infrastructure.processors.HubEventProcessor;
import ru.yandex.practicum.analyzer.domain.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyzerServiceImpl implements AnalyzerService {

    private final ConditionChecker conditionChecker;
    private final Map<Class<?>, HubEventProcessor> hubEventProcessors;
    private final ScenarioRepository scenarioRepository;

    @GrpcClient("hubrouter")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterController;

    @Autowired
    public AnalyzerServiceImpl(
                               List<HubEventProcessor> hubEventProcessors,
                               ScenarioRepository scenarioRepository,
                               ConditionChecker conditionChecker) {
        this.hubEventProcessors = hubEventProcessors.stream()
                .collect(Collectors.toMap(HubEventProcessor::getPayloadClass, Function.identity()));
        this.scenarioRepository = scenarioRepository;
        this.conditionChecker = conditionChecker;
    }

    @Override
    @Transactional(readOnly = true)
    public void handle(SensorsSnapshotAvro snapshotAvro) {
        log.debug("handle snapshot: {}", snapshotAvro);
        List<Scenario> hubScenarios = scenarioRepository.findByHubId(snapshotAvro.getHubId());
        for (Scenario s : hubScenarios) {
            if (checkScenario(s, snapshotAvro)) {
                log.debug("Scenario '{}' is match!", s.getName());
                List<DeviceActionRequest> actionRequests = collectActions(s);
                for (DeviceActionRequest ar : actionRequests) {
                    log.debug("send action: {} {} {}", ar.getAction().getSensorId(), ar.getAction().getType(), ar.getAction().getI());
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        hubRouterController.handleDeviceAction(ar);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private List<DeviceActionRequest> collectActions(Scenario scenario) {
        return scenario.getScenarioActions().stream()
                .map(action -> DeviceActionMapper.toActionRequest(scenario, action))
                .toList();
    }

    private boolean checkScenario(Scenario s, SensorsSnapshotAvro snapshotAvro) {
        log.debug("Check scenario {}", s.getName());
        List<ScenarioCondition> conditions = s.getScenarioConditions();
        Map<String, SensorStateAvro> stateMap = snapshotAvro.getSensorsState();

        for (ScenarioCondition sc : conditions) {
            if (!conditionChecker.check(sc, stateMap)) {
                log.debug("- the condition does not match '{} {} {}' for {}",
                        sc.getCondition().getType(),
                        sc.getCondition().getOperation(),
                        sc.getCondition().getValue(),
                        sc.getSensor().getId());
                return false;
            }
        }
        return true;
    }

    @Override
    public void handle(HubEventAvro event) {
        log.debug("handle hub event {}", event);
        HubEventProcessor processor = getProcessorOrThrow(event);
        processor.handle(event);
    }

    private HubEventProcessor getProcessorOrThrow(HubEventAvro event) {
        Class<?> aClass = event.getPayload().getClass();
        HubEventProcessor processor = hubEventProcessors.get(aClass);
        if (processor == null) {
            throw new IllegalArgumentException("No processor found for payload type: " + aClass.getSimpleName());
        }
        return processor;
    }
}
