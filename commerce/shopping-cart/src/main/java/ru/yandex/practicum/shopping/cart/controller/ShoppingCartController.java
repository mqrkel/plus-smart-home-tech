package ru.yandex.practicum.shopping.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final CartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("GET /api/v1/shopping-cart: получение корзины пользователя {}", username);
        return cartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductInCart(@RequestParam String username,
                                            @RequestBody @NotEmpty Map<UUID, @NotNull @Positive Integer> products) {
        log.info("PUT /api/v1/shopping-cart: username={}, products={}", username, products);
        return cartService.addProductInCart(username, products);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deactivationShoppingCart(@RequestParam String username) {
        log.info("Получен DELETE /api/v1/shopping-cart запрос на деактивацию корзины товаров пользователя {}",
                username);
        cartService.deactivationShoppingCart(username);
    }


    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromCart(@RequestParam String username,
                                                 @RequestBody @NotEmpty List<UUID> productsIds) {
        log.info("POST /api/v1/shopping-cart/remove: удаление продуктов {} из корзины пользователя {}",
                productsIds, username);
        return cartService.removeProductFromCart(username, productsIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityInCart(@RequestParam String username,
                                                @Valid @RequestBody ChangeProductQuantityRequest quantityRequest) {
        log.info("POST /api/v1/shopping-cart/change-quantity: изменение количества товара для пользователя {}",
                username);
        return cartService.changeQuantityInCart(username, quantityRequest);
    }
}