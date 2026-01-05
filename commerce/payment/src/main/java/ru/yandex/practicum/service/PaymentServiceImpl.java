package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.client.StoreFeignClient;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.payment.PaymentState;
import ru.yandex.practicum.interaction.api.store.ProductDto;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final StoreFeignClient shoppingFeignClient;
    private final OrderFeignClient orderFeignClient;
    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.1);

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        BigDecimal productPayment = calculateProductPayment(orderDto);
        BigDecimal feePayment = calculateFeeTotalPayment(productPayment);
        BigDecimal totalPayment = calculateTotalPayment(orderDto);

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(totalPayment)
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(feePayment)
                .state(PaymentState.PENDING)
                .build();
        Payment createdPayment = repository.save(payment);
        return mapper.toPaymentDto(createdPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPayment(OrderDto orderDto) {
        BigDecimal productTotal = calculateProductPayment(orderDto);
        BigDecimal deliveryTotal = orderDto.getDeliveryPrice();
        BigDecimal feePayment = calculateFeeTotalPayment(productTotal);

        return productTotal.add(feePayment).add(deliveryTotal);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateProductPayment(OrderDto orderDto) {
        Map<UUID, Long> productsIds = orderDto.getProducts();

        List<ProductDto> products = shoppingFeignClient.getProductsById(new ArrayList<>(productsIds.keySet()));

        BigDecimal total = BigDecimal.ZERO;
        for (ProductDto product : products) {
            UUID id = product.getProductId();
            Long quantity = productsIds.get(id);

            if (quantity == null) {
                throw new NotEnoughInfoInOrderToCalculateException("Недостаточно информации в заказе для расчёта: " + id);
            }

            BigDecimal price = product.getPrice() != null ? BigDecimal.valueOf(product.getPrice()) : BigDecimal.ZERO;
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));

            total = total.add(itemTotal);
        }
        return total;
    }


    @Override
    @Transactional
    public void refundPayment(UUID paymentId) {
       Payment payment = findPaymentOrThrow(paymentId);
       payment.setState(PaymentState.SUCCESS);
       orderFeignClient.paymentOrder(payment.getOrderId());
       repository.save(payment);
    }

    @Override
    @Transactional
    public void failPayment(UUID paymentId) {
        Payment payment = findPaymentOrThrow(paymentId);
        payment.setState(PaymentState.FAILED);
        orderFeignClient.failPaymentOrder(payment.getOrderId());
        repository.save(payment);
    }

    private BigDecimal calculateFeeTotalPayment(BigDecimal productPayment) {
        return productPayment.multiply(VAT_RATE);
    }

    private Payment findPaymentOrThrow(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платёж не найден id: " + paymentId));
    }
}