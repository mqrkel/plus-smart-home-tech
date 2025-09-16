package ru.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.telemetry.collector.handler.hub.HubEventHandler;
import ru.practicum.telemetry.collector.handler.sensor.SensorEventHandler;
import ru.practicum.telemetry.collector.model.enums.HubEventType;
import ru.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public CollectorServiceImpl(List<SensorEventHandler> sensorEventHandlers,
                                List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));

        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEvent event) {
        log.info("Processing sensor event: {} of type {}", event.getId(), event.getType());
        SensorEventHandler handler = getHandlerOrThrow(event.getType());
        handler.handleEvent(event);
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        log.info("Processing hub event: {} of type {}", event.getHubId(), event.getType());
        HubEventHandler handler = getHandlerOrThrow(event.getType());
        handler.handleEvent(event);
    }

    private SensorEventHandler getHandlerOrThrow(SensorEventType type) {
        SensorEventHandler handler = sensorEventHandlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Cannot find handler for event " + type);
        }
        return handler;
    }

    private HubEventHandler getHandlerOrThrow(HubEventType type) {
        HubEventHandler handler = hubEventHandlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Cannot find handler for event " + type);
        }
        return handler;
    }
}
