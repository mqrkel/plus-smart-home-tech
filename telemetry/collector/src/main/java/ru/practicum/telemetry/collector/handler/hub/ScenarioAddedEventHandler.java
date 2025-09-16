package ru.practicum.telemetry.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.mapper.ActionMapper;
import ru.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.enums.HubEventType;
import ru.practicum.telemetry.collector.model.hub.ScenarioAddedHubEvent;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
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
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro convertToAvro(HubEvent event) {
        ensureCorrectEventType(event, ScenarioAddedHubEvent.class);
        ScenarioAddedHubEvent hubEvent = (ScenarioAddedHubEvent)event;

        List<ScenarioConditionAvro> conditions = hubEvent.getConditions().stream()
                .map(ConditionMapper::toAvro)
                .toList();

        List<DeviceActionAvro> actions = hubEvent.getActions().stream()
                .map(ActionMapper::toAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(hubEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }
}
