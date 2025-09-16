package ru.practicum.telemetry.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.model.sensor.MotionSensorEvent;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEvent event) {
        validateEventType(event, MotionSensorEvent.class);
        MotionSensorEvent sensorEvent = (MotionSensorEvent)event;
        return MotionSensorAvro.newBuilder()
                .setVoltage(sensorEvent.getVoltage())
                .setLinkQuality(sensorEvent.getLinkQuality())
                .setMotion(sensorEvent.isMotion())
                .build();
    }
}
