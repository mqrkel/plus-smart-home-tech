package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void newProduct(@Valid @RequestBody NewProductInWarehouseRequest newRequest) {
        log.info("Добавление нового продукта: productId={}", newRequest.getProductId());
        warehouseService.newProduct(newRequest);
        log.info("Продукт успешно добавлен: productId={}", newRequest.getProductId());
    }

    @PostMapping("/check")
    public BookedProductsDto checkProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Начинаем проверку кол-ва товаров на складе={}", shoppingCartDto);
        BookedProductsDto result = warehouseService.checkQuantityProducts(shoppingCartDto);
        log.info("Проверка кол-ва товара на складе прошла УСПЕШНО = {}, result = {}", shoppingCartDto, result);
        return result;
    }

    @PostMapping("/add")
    public void addProduct(@Valid @RequestBody AddProductToWarehouseRequest addRequest) {
        log.info("Приём товара на склад: productId={}, quantity={}", addRequest.getProductId(), addRequest.getQuantity());
        warehouseService.addQuantityProduct(addRequest);
        log.info("Товар успешно принят: productId={}, quantity={}", addRequest.getProductId(), addRequest.getQuantity());
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Запрос адреса склада");
        AddressDto result = warehouseService.getAddress();
        log.info("Адрес склада предоставлен: {} {} {}, {}", result.getCountry(), result.getCity(), result.getStreet(),
                result.getHouse());

        return result;
    }
}











