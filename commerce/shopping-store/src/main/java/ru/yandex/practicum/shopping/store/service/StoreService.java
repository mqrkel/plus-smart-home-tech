package ru.yandex.practicum.shopping.store.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.interaction.api.store.ProductDto;
import ru.yandex.practicum.interaction.api.store.ProductPageDto;
import ru.yandex.practicum.interaction.api.store.ProductCategory;

import java.util.UUID;

public interface StoreService {
    ProductPageDto getAllProducts(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProductById(UUID productId);

    Boolean setProductQuantityState(UUID productId, String quantityState);

    ProductDto getProductById(UUID productId);
}