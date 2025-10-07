package ru.yandex.practicum.telemetry.collector.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.event.device.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.event.device.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.event.device.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.event.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.event.scenario.ScenarioCondition;
import ru.yandex.practicum.telemetry.collector.model.event.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.impl.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class EventMapper {

    @Mapping(target = "payload", source = ".", qualifiedByName = "toClimatePayload")
    public abstract SensorEventAvro toAvro(ClimateSensorEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toLightPayload")
    public abstract SensorEventAvro toAvro(LightSensorEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toMotionPayload")
    public abstract SensorEventAvro toAvro(MotionSensorEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toSwitchPayload")
    public abstract SensorEventAvro toAvro(SwitchSensorEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toTemperaturePayload")
    public abstract SensorEventAvro toAvro(TemperatureSensorEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toDeviceAddedPayload")
    public abstract HubEventAvro toAvro(DeviceAddedEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toDeviceRemovedPayload")
    public abstract HubEventAvro toAvro(DeviceRemovedEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toScenarioAddedPayload")
    public abstract HubEventAvro toAvro(ScenarioAddedEvent event);

    @Mapping(target = "payload", source = ".", qualifiedByName = "toScenarioRemovedPayload")
    public abstract HubEventAvro toAvro(ScenarioRemovedEvent event);

    @Named("toClimatePayload")
    protected ClimateSensorAvro toClimatePayload(ClimateSensorEvent event) {
        ClimateSensorAvro payload = new ClimateSensorAvro();
        payload.setTemperatureC(event.getTemperatureC());
        payload.setHumidity(event.getHumidity());
        payload.setCo2Level(event.getCo2Level());
        return payload;
    }

    @Named("toLightPayload")
    protected LightSensorAvro toLightPayload(LightSensorEvent event) {
        LightSensorAvro payload = new LightSensorAvro();
        payload.setLinkQuality(event.getLinkQuality());
        payload.setLuminosity(event.getLuminosity());
        return payload;
    }

    @Named("toMotionPayload")
    protected MotionSensorAvro toMotionPayload(MotionSensorEvent event) {
        MotionSensorAvro payload = new MotionSensorAvro();
        payload.setLinkQuality(event.getLinkQuality());
        payload.setMotion(event.getMotion());
        payload.setVoltage(event.getVoltage());
        return payload;
    }

    @Named("toSwitchPayload")
    protected SwitchSensorAvro toSwitchPayload(SwitchSensorEvent event) {
        SwitchSensorAvro payload = new SwitchSensorAvro();
        payload.setState(event.getState());
        return payload;
    }

    @Named("toTemperaturePayload")
    protected TemperatureSensorAvro toTemperaturePayload(TemperatureSensorEvent event) {
        TemperatureSensorAvro payload = new TemperatureSensorAvro();
        payload.setTemperatureC(event.getTemperatureC());
        payload.setTemperatureF(event.getTemperatureF());
        return payload;
    }

    @Named("toDeviceAddedPayload")
    protected DeviceAddedEventAvro toDeviceAddedPayload(DeviceAddedEvent event) {
        DeviceAddedEventAvro payload = new DeviceAddedEventAvro();
        payload.setId(event.getId());
        payload.setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()));
        return payload;
    }

    @Named("toDeviceRemovedPayload")
    protected DeviceRemovedEventAvro toDeviceRemovedPayload(DeviceRemovedEvent event) {
        DeviceRemovedEventAvro payload = new DeviceRemovedEventAvro();
        payload.setId(event.getId());
        return payload;
    }

    @Named("toScenarioAddedPayload")
    protected ScenarioAddedEventAvro toScenarioAddedPayload(ScenarioAddedEvent event) {
        ScenarioAddedEventAvro payload = new ScenarioAddedEventAvro();
        payload.setName(event.getName());
        payload.setConditions(conditionsToMap(event.getConditions()));
        payload.setActions(actionsToMap(event.getActions()));
        return payload;
    }

    protected List<ScenarioConditionAvro> conditionsToMap(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::conditionToMap)
                .toList();
    }

    protected ScenarioConditionAvro conditionToMap(ScenarioCondition condition) {
        ScenarioConditionAvro avro = new ScenarioConditionAvro();
        avro.setSensorId(condition.getSensorId());
        avro.setType(ConditionTypeAvro.valueOf(condition.getType().name()));
        avro.setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()));

        Object value = condition.getValue();
        if (value != null) {
            avro.setValue(value);
        } else {
            avro.setValue(null);
        }
        return avro;
    }

    protected List<DeviceActionAvro> actionsToMap(List<DeviceAction> actions) {
        return actions.stream()
                .map(this::actionToMap)
                .toList();
    }

    protected DeviceActionAvro actionToMap(DeviceAction action) {
        DeviceActionAvro avro = new DeviceActionAvro();
        avro.setSensorId(action.getSensorId());
        avro.setType(ActionTypeAvro.valueOf(action.getType().name()));

        if (action.getValue() != null) {
            avro.setValue(action.getValue());
        } else {
            avro.setValue(null);
        }
        return avro;
    }

    @Named("toScenarioRemovedPayload")
    protected ScenarioRemovedEventAvro toScenarioRemovedPayload(ScenarioRemovedEvent event) {
        ScenarioRemovedEventAvro payload = new ScenarioRemovedEventAvro();
        payload.setName(event.getName());
        return payload;
    }
}