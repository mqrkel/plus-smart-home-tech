package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderFeignClient {
    private final OrderService service;

    @GetMapping
    public Page<OrderDto> getOrders(@RequestParam String username, Pageable pageable) {
        log.info("[GET] Запрос на получения списка заказов, User: {} с пагинацией: {} ", username, pageable);
        return service.getOrders(username, pageable);
    }

    @PutMapping
    public OrderDto createOrder(@Valid @RequestBody CreateNewOrderRequest request) {
        log.info("[PUT] Создание нового заказа: {} ", request);
        return service.createOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest returnRequest) {
        log.info("[POST] Возврат заказа: {} ", returnRequest);
        return service.returnOrder(returnRequest);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Оплата заказа id: {} ", orderId);
        return service.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto failPaymentOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Оплата заказа произошла с ошибкой id: {} ", orderId);
        return service.failPaymentOrder(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Доставка заказа id: {} ", orderId);
        return service.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto failDeliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Доставка заказа произошла с ошибкой id: {} ", orderId);
        return service.failDeliveryOrder(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Завершение заказа с ошибкой id: {} ", orderId);
        return service.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Расчёт стоимости заказа id: {} ", orderId);
        return service.calculateTotalOrder(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateTotalDeliveryOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Расчёт стоимости доставки заказа id: {} ", orderId);
        return service.calculateTotalDeliveryOrder(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Сборка заказа id: {} ", orderId);
        return service.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId) {
        log.info("[POST] Сборка заказа произошла с ошибкой id: {} ", orderId);
        return service.failAssemblyOrder(orderId);
    }
}