package ru.yandex.practicum.interaction.api.exception.store;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class ProductNotFoundException extends ApiException {
    public ProductNotFoundException(String message, String userMessage) {
        super(message, HttpStatus.NOT_FOUND, userMessage);
    }
}
