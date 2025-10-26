package ru.yandex.practicum.interaction.api.exception.warehouse;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.interaction.api.exception.ApiException;

public class ProductInShoppingCartLowQuantityInWarehouse extends ApiException {
    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message, HttpStatus.BAD_REQUEST, userMessage);
    }
}
