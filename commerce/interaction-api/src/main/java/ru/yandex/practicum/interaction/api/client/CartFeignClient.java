package ru.yandex.practicum.interaction.api.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.cart.ShoppingCartDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartFeignClient {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String userName) throws FeignException;

    @PutMapping
    ShoppingCartDto addProductInCart(@RequestParam String userName,
                                     @Valid @RequestBody ShoppingCartDto shoppingCartDto) throws FeignException;

    @DeleteMapping
    void deactivationShoppingCart(@RequestParam String userName) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromCart(@RequestParam String userName,
                                          @Valid @RequestBody List<UUID> productsIds) throws FeignException;

    @PostMapping("change-quantity")
    ShoppingCartDto changeQuantityInCart(@RequestParam String userName,
                                         @Valid @RequestBody ChangeProductQuantityRequest quantityRequest) throws FeignException;

}