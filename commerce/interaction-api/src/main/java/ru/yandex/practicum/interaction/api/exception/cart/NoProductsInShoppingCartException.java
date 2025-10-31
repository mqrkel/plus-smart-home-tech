package ru.yandex.practicum.interaction.api.exception.cart;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class NoProductsInShoppingCartException extends ApiException {
    public NoProductsInShoppingCartException(String message, String userMessage) {
        super(message, HttpStatus.BAD_REQUEST, userMessage);
    }
}
