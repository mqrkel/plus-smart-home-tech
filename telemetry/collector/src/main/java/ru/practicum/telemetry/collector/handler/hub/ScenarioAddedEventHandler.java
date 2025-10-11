package ru.practicum.telemetry.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.mapper.ScenarioActionMapper;
import ru.practicum.telemetry.collector.mapper.ScenarioConditionMapper;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getEventType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    };

    @Override
    protected ScenarioAddedEventAvro toAvro(HubEventProto event) {
        validateEventType(event);
        ScenarioAddedEventProto hubEvent = event.getScenarioAdded();

        List<ScenarioConditionAvro> conditions = hubEvent.getConditionsList().stream()
                .map(ScenarioConditionMapper::toAvro)
                .toList();

        List<DeviceActionAvro> actions = hubEvent.getActionsList().stream()
                .map(ScenarioActionMapper::toAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(hubEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }
}
