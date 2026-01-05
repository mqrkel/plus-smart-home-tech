package ru.yandex.practicum.service;


import ru.yandex.practicum.interaction.api.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal calculateDeliveryCost(OrderDto orderDto);
}