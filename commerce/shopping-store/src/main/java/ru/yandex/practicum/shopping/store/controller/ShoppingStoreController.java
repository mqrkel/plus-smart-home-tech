package ru.yandex.practicum.shopping.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.ProductPageDto;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.shopping.store.service.StoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {

    private final StoreService storeService;

    @GetMapping
    public ProductPageDto getAllProducts(@RequestParam ProductCategory category, Pageable pageable) {
        log.info("GET /api/v1/shopping-store: category={}, pageable={}", category, pageable);
        return storeService.getAllProducts(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store: create productName={}", productDto.getProductName());
        return storeService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("POST /api/v1/shopping-store: update productName={}", productDto.getProductName());
        return storeService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProductById(@RequestBody UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore: productId={}", productId);
        return storeService.removeProductById(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam String quantityState
    ) {
        log.info("POST /api/v1/shopping-store/quantityState: productId={}, quantityState={}", productId, quantityState);
        return storeService.setProductQuantityState(productId, quantityState);
    }


    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("GET /api/v1/shopping-store/{productId}: productId={}", productId);
        return storeService.getProductById(productId);
    }
}