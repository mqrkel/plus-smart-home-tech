package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {

    @Id
    @NotNull
    @Column(name = "product_id", nullable = false)
    UUID productId;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    Long quantity = 0L;

    @NotNull
    @Positive
    @Column(nullable = false)
    Double weight;

    @NotNull
    @Column(nullable = false)
    Boolean fragile;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dimension_id", referencedColumnName = "id", unique = true)
    Dimension dimension;

}