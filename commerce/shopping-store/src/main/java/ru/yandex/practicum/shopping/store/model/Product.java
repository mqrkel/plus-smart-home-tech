package ru.yandex.practicum.shopping.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.api.store.ProductCategory;
import ru.yandex.practicum.interaction.api.store.ProductState;
import ru.yandex.practicum.interaction.api.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    UUID productId;

    @NotBlank
    @Column(name = "product_name", nullable = false)
    String productName;

    @NotBlank
    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "image_src")
    String imageSrc; // nullable по спецификации

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    QuantityState quantityState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    ProductState productState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    ProductCategory productCategory;

    @NotNull
    @DecimalMin(value = "1.0", message = "Цена должна быть минимум 1")
    @Column(name = "price", nullable = false)
    BigDecimal price;
}