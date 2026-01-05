package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.client.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryFeignClient {

    private final DeliveryService service;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto) {
        log.info("[PUT] Создание новой доставки: {}", deliveryDto);
        return service.planDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody @Valid DeliveryIdRequest request) {
        log.info("[POST] Подтверждение успешной доставки id заказа: {}", request.deliveryId());
        service.successfulDelivery(request.deliveryId());
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody @Valid DeliveryIdRequest request) {
        log.info("[POST] Подтверждение получения заказа id: {}", request.deliveryId());
        service.pickedDelivery(request.deliveryId());
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody @Valid DeliveryIdRequest request) {
        log.info("[POST] Неудачное вручение товара id заказа: {}", request.deliveryId());
        service.failedDelivery(request.deliveryId());
    }

    @PostMapping("/cost")
    public BigDecimal deliveryCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("[POST] Расчёт полной стоимости доставки заказа: {}", orderDto);
        return service.calculateDeliveryCost(orderDto);
    }
}
