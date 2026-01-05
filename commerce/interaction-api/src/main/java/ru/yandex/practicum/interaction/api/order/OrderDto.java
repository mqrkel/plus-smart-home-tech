package ru.yandex.practicum.interaction.api.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID orderId;
    private UUID shoppingCartId;
    @NotNull
    private Map<UUID, Long > products;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private BigDecimal deliveryWeight;
    private BigDecimal deliveryVolume;
    private boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
}