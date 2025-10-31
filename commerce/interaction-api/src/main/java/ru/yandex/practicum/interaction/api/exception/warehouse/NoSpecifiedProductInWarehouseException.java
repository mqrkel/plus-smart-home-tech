package ru.yandex.practicum.interaction.api.exception.warehouse;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class NoSpecifiedProductInWarehouseException extends ApiException {
    public NoSpecifiedProductInWarehouseException(String message, String userMessage) {
        super(message, HttpStatus.BAD_REQUEST, userMessage);
    }
}
