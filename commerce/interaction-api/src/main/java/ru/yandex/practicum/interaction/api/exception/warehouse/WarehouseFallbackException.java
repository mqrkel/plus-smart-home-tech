package ru.yandex.practicum.interaction.api.exception.warehouse;

public class WarehouseFallbackException extends RuntimeException {
    public WarehouseFallbackException(String message) {
        super(message);
    }
}
