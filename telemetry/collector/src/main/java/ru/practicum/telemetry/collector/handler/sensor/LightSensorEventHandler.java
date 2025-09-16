package ru.practicum.telemetry.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.model.sensor.LightSensorEvent;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.practicum.telemetry.collector.model.enums.SensorEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEvent event) {
        validateEventType(event, LightSensorEvent.class);
        LightSensorEvent sensorEvent = (LightSensorEvent)event;
        return LightSensorAvro.newBuilder()
                .setLuminosity(sensorEvent.getLuminosity())
                .setLinkQuality(sensorEvent.getLinkQuality())
                .build();
    }
}
