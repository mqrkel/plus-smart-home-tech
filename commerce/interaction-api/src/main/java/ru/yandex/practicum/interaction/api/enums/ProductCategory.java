package ru.yandex.practicum.interaction.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCategory {
    LIGHTING, CONTROL, SENSORS;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static ProductCategory fromValue(String value) {
        return ProductCategory.valueOf(value.toUpperCase());
    }
}
