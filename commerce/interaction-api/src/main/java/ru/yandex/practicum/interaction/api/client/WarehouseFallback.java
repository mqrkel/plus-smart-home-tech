package ru.yandex.practicum.interaction.api.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.interaction.api.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.warehouse.WarehouseFallbackException;
import ru.yandex.practicum.interaction.api.warehouse.*;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseFallback implements WarehouseFeignClient {

    @Override
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        throw new WarehouseFallbackException("Fallback response: сервис WAREHOUSE временно недоступен");
    }

    @Override
    public BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto) {
        throw new WarehouseFallbackException("Fallback response: сервис WAREHOUSE временно недоступен");
    }

    @Override
    public void addQuantityProduct(AddProductToWarehouseRequest addRequest) {
        throw new WarehouseFallbackException("Fallback response: сервис WAREHOUSE временно недоступен");
    }

    @Override
    public AddressDto getAddress() {
        throw new WarehouseFallbackException("Fallback response: сервис WAREHOUSE временно недоступен");
    }

    @Override
    public void shippedToDelivery(ShipperToDeliveryRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Warehouse temporarily unavailable. Please try again later.");
    }
}
