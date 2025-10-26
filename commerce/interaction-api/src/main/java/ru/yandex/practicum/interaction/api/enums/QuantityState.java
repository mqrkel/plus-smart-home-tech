package ru.yandex.practicum.interaction.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuantityState {
    ENDED, FEW, ENOUGH, MANY;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static QuantityState fromValue(String value) {
        return QuantityState.valueOf(value.toUpperCase());
    }
}
