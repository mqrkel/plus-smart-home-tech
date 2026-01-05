package ru.yandex.practicum.interaction.api.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.store.ProductDto;
import ru.yandex.practicum.interaction.api.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.store.ProductCategory;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreFeignClient {
    @GetMapping
    List<ProductDto> getAllProducts(@RequestParam ProductCategory category) throws FeignException;

    @PutMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean removeProductById(@Valid @RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@Valid @RequestBody SetProductQuantityStateRequest request) throws FeignException;

    @PostMapping("/productsByIds")
    List<ProductDto> getProductsById(@RequestBody List<UUID> productIds);

}
