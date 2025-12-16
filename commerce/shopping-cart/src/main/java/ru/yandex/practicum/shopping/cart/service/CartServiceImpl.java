package ru.yandex.practicum.shopping.cart.service;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.exception.cart.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.exception.cart.ShoppingCartDeactivateException;
import ru.yandex.practicum.shopping.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shopping.cart.model.ShoppingCart;
import ru.yandex.practicum.interaction.api.cart.ShoppingCartState;
import ru.yandex.practicum.shopping.cart.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper mapper;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsernameForEmpty(username);
        ShoppingCart cart = getOrCreateCart(username);
        validateCartStatus(cart);
        return mapper.mapToCartDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductInCart(String username, Map<UUID, Integer> products) {
        checkUsernameForEmpty(username);

        if (products == null || products.isEmpty()) {
            throw new BadRequestException("Список продуктов не может быть пустым");
        }

        ShoppingCart cart = getOrCreateCart(username);
        validateCartStatus(cart);

        checkAvailableProductsInWarehouse(cart.getCartId(), products);

        products.forEach((id, quantity) -> cart.getProducts().merge(id, quantity, Integer::sum));

        return mapper.mapToCartDto(cart);
    }

    @Override
    @Transactional
    public void deactivationShoppingCart(String username) {
        checkUsernameForEmpty(username);
        ShoppingCart cart = getOrCreateCart(username);
        cart.setStatus(ShoppingCartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductFromCart(String username, List<UUID> productsIds) {
        checkUsernameForEmpty(username);
        ShoppingCart cart = getOrCreateCart(username);
        validateCartStatus(cart);

        productsIds.forEach(cart.getProducts()::remove);

        return mapper.mapToCartDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantityInCart(String username, ChangeProductQuantityRequest quantityRequest) {
        checkUsernameForEmpty(username);

        if (quantityRequest == null
            || quantityRequest.getProductId() == null
            || quantityRequest.getNewQuantity() == null) {
            throw new BadRequestException("productId и newQuantity должны быть заполнены");
        }

        ShoppingCart cart = getOrCreateCart(username);
        validateCartStatus(cart);

        checkAvailableProductsInWarehouse(cart.getCartId(),
                Map.of(quantityRequest.getProductId(), quantityRequest.getNewQuantity()));

        cart.getProducts().put(quantityRequest.getProductId(), quantityRequest.getNewQuantity());

        return mapper.mapToCartDto(cart);
    }

    private void checkUsernameForEmpty(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException(
                    "Username is empty",
                    "Имя пользователя не может быть пустым"
            );

        }
    }

    private ShoppingCart getOrCreateCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> shoppingCartRepository.save(
                        ShoppingCart.builder()
                                .username(username)
                                .build()
                ));
    }

    private void validateCartStatus(ShoppingCart cart) {
        if (cart.getStatus() == ShoppingCartState.DEACTIVATE) {
            throw new ShoppingCartDeactivateException(
                    "Корзина пользователя деактивирована",
                    "Ваша корзина сейчас недоступна"
            );
        }
    }

    private void checkAvailableProductsInWarehouse(UUID shoppingCartId, Map<UUID, Integer> products) {
        ShoppingCartDto shoppingCartDto = ShoppingCartDto.builder()
                .cartId(shoppingCartId)
                .products(products)
                .build();
        warehouseFeignClient.checkQuantityProducts(shoppingCartDto);
    }
}
