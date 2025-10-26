package ru.yandex.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    ShoppingCart mapToCart(ShoppingCartDto shoppingCartDto);

    ShoppingCartDto mapToCartDto(ShoppingCart shoppingCart);
}