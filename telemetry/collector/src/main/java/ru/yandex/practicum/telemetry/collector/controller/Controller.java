package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.controller.handler.device.DeviceEvent;
import ru.yandex.practicum.telemetry.collector.controller.handler.sensor.SensorEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class Controller extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<HubEventProto.PayloadCase, DeviceEvent> deviceHandlers;
    private final Map<SensorEventProto.PayloadCase, SensorEvent> sensorHandlers;

    public Controller(Set<DeviceEvent> deviceHandlers, Set<SensorEvent> sensorHandlers) {
        this.deviceHandlers = deviceHandlers.stream()
                .collect(Collectors.toMap(DeviceEvent::getMessageType, Function.identity()));
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEvent::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> response) {
        try {
            SensorEvent event = sensorHandlers.get(request.getPayloadCase());
            event.handle(request);
            response.onNext(Empty.getDefaultInstance());
            response.onCompleted();
        } catch (Exception e) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> response) {
        try {
            DeviceEvent event = deviceHandlers.get(request.getPayloadCase());
            event.handle(request);
            response.onNext(Empty.getDefaultInstance());
            response.onCompleted();
        } catch (Exception e) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
