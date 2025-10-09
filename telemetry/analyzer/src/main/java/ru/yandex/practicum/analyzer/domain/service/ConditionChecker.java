package ru.yandex.practicum.analyzer.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.domain.model.Condition;
import ru.yandex.practicum.analyzer.domain.model.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@Component
public class ConditionChecker {

    private final Map<ConditionOperationAvro, BiFunction<Integer, Integer, Boolean>> operations = Map.of(
            ConditionOperationAvro.LOWER_THAN, (sensorValue, threshold) -> sensorValue < threshold,
            ConditionOperationAvro.GREATER_THAN, (sensorValue, threshold) -> sensorValue > threshold,
            ConditionOperationAvro.EQUALS, Integer::equals
    );

    public boolean evaluate(ConditionOperationAvro type, int sensorValue, int threshold) {
        return operations.get(type).apply(sensorValue, threshold);
    }

    public boolean check(ScenarioCondition scenarioCondition, Map<String, SensorStateAvro> stateMap) {
        log.debug("Check scenario condition");
        String sensorId = scenarioCondition.getSensor().getId();
        SensorStateAvro sensorStateAvro = stateMap.get(sensorId);
        if (sensorStateAvro == null)
            return false;

        Condition condition = scenarioCondition.getCondition();
        ConditionOperationAvro operator = condition.getOperation();
        int sensorValue = getSensorValue(condition.getType(), sensorStateAvro.getPayload());

        boolean result = evaluate(operator, sensorValue, condition.getValue());
        log.debug("{}: value: {}  operation: {} threshold: {} result: {}",
                condition.getType(),
                sensorValue,
                condition.getOperation(),
                condition.getValue(),
                result);

        return result;
    }

    private static int getSensorValue(ConditionTypeAvro conditionType, Object payload) {

        return switch (conditionType) {
            case MOTION     -> ((MotionSensorAvro) payload).getMotion() ? 1 : 0;
            case LUMINOSITY -> ((LightSensorAvro) payload).getLuminosity();
            case SWITCH     -> ((SwitchSensorAvro) payload).getState() ? 1 : 0;
            case TEMPERATURE -> (payload instanceof ClimateSensorAvro)
                    ? ((ClimateSensorAvro) payload).getTemperatureC()
                    : ((TemperatureSensorAvro) payload).getTemperatureC();
            case CO2LEVEL   -> ((ClimateSensorAvro) payload).getCo2Level();
            case HUMIDITY   -> ((ClimateSensorAvro) payload).getHumidity();
        };
    }
}
