package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.client.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentFeignClient {
    private final PaymentService service;

    @PostMapping
    public PaymentDto createPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("[POST] Формирование оплаты для заказа: {}", orderDto);
        return service.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("[POST] Расчёт полной стоимости заказа: {}", orderDto);
        return service.calculateTotalPayment(orderDto);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateProductPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("[POST] Расчёт стоимости товаров в заказе: {}", orderDto);
        return service.calculateProductPayment(orderDto);
    }

    @PostMapping("/refund")
    public void refundPayment(@RequestBody @NotNull UUID paymentId) {
        log.info("[POST] Подтверждение успешной оплаты id платежа: {}", paymentId);
        service.refundPayment(paymentId);
    }

    @PostMapping("/failed")
    public void failPayment(@RequestBody @NotNull UUID paymentId) {
        log.info("[POST] Отказ в оплате id платежа: {}", paymentId);
        service.failPayment(paymentId);
    }
}