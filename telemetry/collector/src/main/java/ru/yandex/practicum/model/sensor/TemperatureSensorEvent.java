package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.sensor.enums.SensorEventType;

import java.time.Instant;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemperatureSensorEvent extends SensorEvent {

    @NotBlank
    String id;

    @NotBlank
    String hubId;

    @Builder.Default
    Instant timestamp = Instant.now();

    @NotNull
    Integer temperatureC;

    @NotNull
    Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
