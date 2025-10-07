package ru.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.telemetry.collector.handler.hub.HubEventHandler;
import ru.practicum.telemetry.collector.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorServiceImpl implements CollectorService {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public CollectorServiceImpl(List<SensorEventHandler> sensorEventHandlers,
                                List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));

        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }

    private SensorEventHandler getHandlerOrThrow(SensorEventProto.PayloadCase type) {
        SensorEventHandler handler = sensorEventHandlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Cannot find handler for event " + type);
        }
        return handler;
    }

    private HubEventHandler getHandlerOrThrow(HubEventProto.PayloadCase type) {
        HubEventHandler handler = hubEventHandlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Cannot find handler for event " + type);
        }
        return handler;
    }

    @Override
    public void collectSensorEvent(SensorEventProto event) {
        SensorEventHandler handler = getHandlerOrThrow(event.getPayloadCase());
        handler.handleEvent(event);
    }

    @Override
    public void collectHubEvent(HubEventProto event) {
        HubEventHandler handler = getHandlerOrThrow(event.getPayloadCase());
        handler.handleEvent(event);
    }
}
