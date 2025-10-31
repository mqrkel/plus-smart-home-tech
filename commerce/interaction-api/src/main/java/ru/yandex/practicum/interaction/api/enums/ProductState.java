package ru.yandex.practicum.interaction.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductState {
    ACTIVE, DEACTIVATE;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static ProductState fromValue(String value) {
        return ProductState.valueOf(value.toUpperCase());
    }
}
