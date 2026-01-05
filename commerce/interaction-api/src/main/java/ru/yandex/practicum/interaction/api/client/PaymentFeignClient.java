package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {
    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/productCost")
    BigDecimal calculateProductPayment(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/refund")
    void refundPayment(@RequestBody @NotNull UUID paymentId);

    @PostMapping("/failed")
    void failPayment(@RequestBody @NotNull UUID paymentId);
}