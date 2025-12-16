package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    private String username;

    @Column(name = "cart_id")
    private UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long > products;

    private UUID paymentId;

    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Column(precision = 19, scale = 4)
    private BigDecimal deliveryWeight;

    @Column(precision = 19, scale = 4)
    private BigDecimal deliveryVolume;

    private boolean fragile;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal deliveryPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal productPrice;
}