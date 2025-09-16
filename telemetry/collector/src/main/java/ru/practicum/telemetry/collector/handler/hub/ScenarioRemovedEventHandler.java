package ru.practicum.telemetry.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.enums.HubEventType;
import ru.practicum.telemetry.collector.model.hub.ScenarioRemovedHubEvent;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    protected ScenarioRemovedEventAvro convertToAvro(HubEvent event) {
        ensureCorrectEventType(event, ScenarioRemovedHubEvent.class);
        ScenarioRemovedHubEvent hubEvent = (ScenarioRemovedHubEvent)event;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(hubEvent.getName())
                .build();
    }
}
