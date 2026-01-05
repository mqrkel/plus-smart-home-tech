package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    record DeliveryIdRequest(@NotNull UUID deliveryId) {
    }

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody @Valid DeliveryIdRequest request);

    @PostMapping("/picked")
    void pickedDelivery(@RequestBody @Valid DeliveryIdRequest request);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody @Valid DeliveryIdRequest request);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody @Valid OrderDto orderDto);
}
