package ru.yandex.practicum.interaction.api.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.order.ProductReturnRequest;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderFeignClient {
    @GetMapping
    Page<OrderDto> getOrders(@RequestParam String username, Pageable pageable);

    @PutMapping
    OrderDto createOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto failPaymentOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto failDeliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateTotalDeliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId);
}