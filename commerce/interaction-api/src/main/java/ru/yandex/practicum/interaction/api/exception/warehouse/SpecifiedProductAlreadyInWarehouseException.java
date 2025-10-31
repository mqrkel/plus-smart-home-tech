package ru.yandex.practicum.interaction.api.exception.warehouse;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class SpecifiedProductAlreadyInWarehouseException extends ApiException {
    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message, HttpStatus.BAD_REQUEST, userMessage);
    }
}
