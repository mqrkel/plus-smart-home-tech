package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.api.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void newProduct(NewProductInWarehouseRequest newRequest);

    BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto);

    void addQuantityProduct(AddProductToWarehouseRequest addRequest);

    AddressDto getAddress();

    void shippedToDelivery(ShipperToDeliveryRequest request);

    void returnProducts(Map<UUID, Long> products);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);
}