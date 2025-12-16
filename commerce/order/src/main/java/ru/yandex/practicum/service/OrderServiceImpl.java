package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.client.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.client.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.delivery.DeliveryState;
import ru.yandex.practicum.interaction.api.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.order.OrderState;
import ru.yandex.practicum.interaction.api.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.warehouse.BookedProductsDto;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final WarehouseFeignClient warehouseClient;
    private final PaymentFeignClient paymentClient;
    private final DeliveryFeignClient deliveryClient;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        return repository.findByUsername(username, pageable)
                .map(mapper::toOrderDto);
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProductsDto = warehouseClient.checkQuantityProducts(request.getShoppingCart());

        Order order = createNewOrder(request, bookedProductsDto);
        repository.save(order);

        DeliveryDto createdDelivery = planDelivery(order, request.getDeliveryAddress());
        order.setDeliveryId(createdDelivery.getDeliveryId());

        processPayment(order);
        Order savedOrder = repository.save(order);

        return mapper.toOrderDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        warehouseClient.returnProducts(returnRequest.getProducts());
        return changeState(returnRequest.getOrderId(), OrderState.PRODUCT_RETURNED);
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {
        return changeState(orderId, OrderState.PAID);
    }

    @Override
    @Transactional
    public OrderDto failPaymentOrder(UUID orderId) {
        return changeState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    @Transactional
    public OrderDto deliveryOrder(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERED);
    }

    @Override
    @Transactional
    public OrderDto failDeliveryOrder(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    @Transactional
    public OrderDto completedOrder(UUID orderId) {
        return changeState(orderId, OrderState.COMPLETED);
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLED);
    }

    @Override
    @Transactional
    public OrderDto failAssemblyOrder(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        BigDecimal totalPrice = paymentClient.calculateTotalPayment(mapper.toOrderDto(order));
        order.setTotalPrice(totalPrice);
        return mapper.toOrderDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalDeliveryOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        BigDecimal deliveryPrice = deliveryClient.deliveryCost(mapper.toOrderDto(order));
        order.setDeliveryPrice(deliveryPrice);
        return mapper.toOrderDto(repository.save(order));
    }


    private Order createNewOrder(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto) {
        Map<UUID, Long> productsAsLong = request.getShoppingCart().getProducts()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().longValue()
                ));

        return Order.builder()
                .shoppingCartId(request.getShoppingCart().getCartId())
                .products(productsAsLong)
                .state(OrderState.NEW)
                .deliveryWeight(BigDecimal.valueOf(bookedProductsDto.getDeliveryWeight()))
                .deliveryVolume(BigDecimal.valueOf(bookedProductsDto.getDeliveryVolume()))
                .fragile(Boolean.TRUE.equals(bookedProductsDto.getFragile()))
                .build();
    }

    private DeliveryDto planDelivery(Order order, AddressDto deliveryAddress) {
        AddressDto warehouseAddress = warehouseClient.getAddress();
        DeliveryDto delivery = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseAddress)
                .toAddress(deliveryAddress)
                .state(DeliveryState.CREATED)
                .build();
        return deliveryClient.planDelivery(delivery);
    }

    private void processPayment(Order order) {
        OrderDto orderDto = mapper.toOrderDto(order);
        var createdPayment = paymentClient.createPayment(orderDto);
        order.setPaymentId(createdPayment.getPaymentId());
        order.setTotalPrice(createdPayment.getTotalPayment());
        order.setDeliveryPrice(createdPayment.getDeliveryTotal());
        order.setProductPrice(paymentClient.calculateProductPayment(orderDto));
    }

    private OrderDto changeState(UUID orderId, OrderState newState) {
        Order order = findOrderOrThrow(orderId);
        order.setState(newState);
        return mapper.toOrderDto(repository.save(order));
    }

    private Order findOrderOrThrow(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ id: " + orderId + " не найден"));
    }
}
