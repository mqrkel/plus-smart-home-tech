package ru.practicum.telemetry.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        validateEventType(event);
        LightSensorProto sensorEvent = event.getLightSensorEvent();
        return LightSensorAvro.newBuilder()
                .setLuminosity(sensorEvent.getLuminosity())
                .setLinkQuality(sensorEvent.getLinkQuality())
                .build();
    }
}
