package ru.practicum.telemetry.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        validateEventType(event);
        MotionSensorProto sensorEvent = event.getMotionSensorEvent();
        return MotionSensorAvro.newBuilder()
                .setVoltage(sensorEvent.getVoltage())
                .setLinkQuality(sensorEvent.getLinkQuality())
                .setMotion(sensorEvent.getMotion())
                .build();
    }
}
