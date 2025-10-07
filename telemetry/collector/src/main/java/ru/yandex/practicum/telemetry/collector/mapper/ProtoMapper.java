package ru.yandex.practicum.telemetry.collector.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProtoMapper {

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "timestampToInstant")
    @Mapping(target = "payload", source = "sensorEvent", qualifiedByName = "mapSensorPayload")
    SensorEventAvro toAvro(SensorEventProto sensorEvent);

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "timestampToInstant")
    @Mapping(target = "payload", source = "hubEvent", qualifiedByName = "mapHubPayload")
    HubEventAvro toAvro(HubEventProto hubEvent);

    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    @Named("mapSensorPayload")
    default Object mapSensorPayload(SensorEventProto sensorEvent) {
        if (sensorEvent.hasMotionSensorEvent()) {
            return map(sensorEvent.getMotionSensorEvent());
        } else if (sensorEvent.hasTemperatureSensorEvent()) {
            return map(sensorEvent.getTemperatureSensorEvent());
        } else if (sensorEvent.hasLightSensorEvent()) {
            return map(sensorEvent.getLightSensorEvent());
        } else if (sensorEvent.hasClimateSensorEvent()) {
            return map(sensorEvent.getClimateSensorEvent());
        } else if (sensorEvent.hasSwitchSensorEvent()) {
            return map(sensorEvent.getSwitchSensorEvent());
        }
        return null;
    }

    @Named("mapHubPayload")
    default Object mapHubPayload(HubEventProto hubEvent) {
        if (hubEvent.hasDeviceAdded()) {
            return map(hubEvent.getDeviceAdded());
        } else if (hubEvent.hasDeviceRemoved()) {
            return map(hubEvent.getDeviceRemoved());
        } else if (hubEvent.hasScenarioAdded()) {
            return map(hubEvent.getScenarioAdded());
        } else if (hubEvent.hasScenarioRemoved()) {
            return map(hubEvent.getScenarioRemoved());
        }
        return null;
    }

    MotionSensorAvro map(MotionSensorProto proto);

    TemperatureSensorAvro map(TemperatureSensorProto proto);

    LightSensorAvro map(LightSensorProto proto);

    ClimateSensorAvro map(ClimateSensorProto proto);

    SwitchSensorAvro map(SwitchSensorProto proto);

    @Mapping(target = "type", source = "type", qualifiedByName = "mapDeviceType")
    DeviceAddedEventAvro map(DeviceAddedEventProto proto);

    DeviceRemovedEventAvro map(DeviceRemovedEventProto proto);

    @Mapping(target = "conditions", source = "conditionList")
    @Mapping(target = "actions", source = "actionList")
    ScenarioAddedEventAvro map(ScenarioAddedEventProto proto);

    ScenarioRemovedEventAvro map(ScenarioRemovedEventProto proto);

    @Mapping(target = "operation", source = "operation", qualifiedByName = "mapConditionOperation")
    @Mapping(target = "value", source = "proto", qualifiedByName = "mapConditionValue")
    ScenarioConditionAvro map(ScenarioConditionProto proto);

    @Mapping(target = "type", source = "type", qualifiedByName = "mapActionType")
    DeviceActionAvro map(DeviceActionProto proto);

    default ConditionOperationAvro mapConditionOperation(ConditionOperationProto operation) {
        if (operation == ConditionOperationProto.UNRECOGNIZED) {
            return null;
        }
        return ConditionOperationAvro.valueOf(operation.name());
    }

    default ActionTypeAvro mapActionType(ActionTypeProto type) {
        if (type == ActionTypeProto.UNRECOGNIZED) {
            return null;
        }
        return ActionTypeAvro.valueOf(type.name());
    }

    default DeviceTypeAvro mapDeviceType(DeviceTypeProto type) {
        if (type == DeviceTypeProto.UNRECOGNIZED) {
            return null;
        }
        return DeviceTypeAvro.valueOf(type.name());
    }

    default ConditionTypeAvro mapConditionType(ConditionTypeProto type) {
        if (type == ConditionTypeProto.UNRECOGNIZED) {
            return null;
        }
        return ConditionTypeAvro.valueOf(type.name());
    }

    @Named("mapConditionOperation")
    default ConditionOperationAvro mapConditionOperationNamed(ConditionOperationProto operation) {
        return mapConditionOperation(operation);
    }

    @Named("mapActionType")
    default ActionTypeAvro mapActionTypeNamed(ActionTypeProto type) {
        return mapActionType(type);
    }

    @Named("mapDeviceType")
    default DeviceTypeAvro mapDeviceTypeNamed(DeviceTypeProto type) {
        return mapDeviceType(type);
    }

    @Named("mapConditionValue")
    default Object mapConditionValue(ScenarioConditionProto proto) {
        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                return proto.getBoolValue();
            case INT_VALUE:
                return proto.getIntValue();
            default:
                return null;
        }
    }
}