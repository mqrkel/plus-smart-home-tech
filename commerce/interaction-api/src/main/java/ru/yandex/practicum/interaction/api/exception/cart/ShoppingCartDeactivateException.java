package ru.yandex.practicum.interaction.api.exception.cart;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class ShoppingCartDeactivateException extends ApiException {
    public ShoppingCartDeactivateException(String message, String userMessage) {
        super(message, HttpStatus.CONFLICT, userMessage);
    }
}
