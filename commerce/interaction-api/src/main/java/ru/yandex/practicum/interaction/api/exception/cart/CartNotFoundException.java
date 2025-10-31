package ru.yandex.practicum.interaction.api.exception.cart;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class CartNotFoundException extends ApiException {
    public CartNotFoundException(String message, String userMessage) {
        super(message, HttpStatus.NOT_FOUND, userMessage);
    }
}
