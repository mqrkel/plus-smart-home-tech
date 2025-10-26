package ru.yandex.practicum.interaction.api.exception.cart;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class NotAuthorizedUserException extends ApiException {
    public NotAuthorizedUserException(String message, String userMessage) {
        super(message, HttpStatus.UNAUTHORIZED, userMessage);
    }
}
