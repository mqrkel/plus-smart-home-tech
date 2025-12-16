package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.delivery.DeliveryState;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.order.OrderDto;
import ru.yandex.practicum.interaction.api.warehouse.ShipperToDeliveryRequest;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    private static final BigDecimal BASE_COST = BigDecimal.valueOf(5);
    private static final BigDecimal WAREHOUSE_2_ADDRESS_MULTIPLIER = BigDecimal.valueOf(2);
    private static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal STREET_MULTIPLIER = BigDecimal.valueOf(0.2);


    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toDelivery(deliveryDto);
        Delivery savedDelivery = repository.save(delivery);
        return mapper.toDeliveryDto(savedDelivery);
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.DELIVERED);
        repository.save(delivery);
        orderClient.deliveryOrder(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        UUID orderId = delivery.getOrderId();
        delivery.setState(DeliveryState.IN_PROGRESS);
        orderClient.assemblyOrder(orderId);
        warehouseClient.shippedToDelivery(new ShipperToDeliveryRequest(orderId, deliveryId));
        repository.save(delivery);
    }

    @Override
    @Transactional
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);
        delivery.setState(DeliveryState.FAILED);
        orderClient.failDeliveryOrder(delivery.getOrderId());
        repository.save(delivery);
    }

    /**
     * Рассчитывает стоимость доставки заказа с учётом различных факторов.
     *
     * <p>Алгоритм расчёта:
     * <ol>
     *     <li>Берётся базовая стоимость доставки {@link #BASE_COST}.</li>
     *     <li>Увеличение стоимости в зависимости от адреса склада:
     *         <ul>
     *             <li>Если улица склада содержит "ADDRESS_2", стоимость умножается на 2
     *                 (т.е. прибавляется текущая стоимость * 2).</li>
     *             <li>Если бы использовался "ADDRESS_1", стоимость увеличивалась бы на 100% (не используется в коде).</li>
     *         </ul>
     *     </li>
     *     <li>Если заказ помечен как хрупкий ({@link OrderDto#isFragile()}),
     *         к текущей стоимости добавляется 20% (умножение на {@link #FRAGILE_MULTIPLIER}).</li>
     *     <li>К стоимости прибавляется стоимость веса заказа:
     *         <pre>
     *             orderDto.getDeliveryWeight() * WEIGHT_MULTIPLIER
     *         </pre>
     *     </li>
     *     <li>К стоимости прибавляется стоимость объёма заказа:
     *         <pre>
     *             orderDto.getDeliveryVolume() * VOLUME_MULTIPLIER
     *         </pre>
     *     </li>
     *     <li>Если улицы склада и доставки различаются, к текущей стоимости добавляется 20%
     *         (умножение на {@link #STREET_MULTIPLIER}).</li>
     * </ol>
     *
     * <p>Все вычисления выполняются с использованием {@link BigDecimal} для сохранения точности денежных расчётов.
     * На каждом этапе расчёта логируются значения стоимости для удобства отладки.</p>
     *
     * @param orderDto объект {@link OrderDto}, содержащий данные о заказе:
     *                 идентификатор доставки, вес, объём и признак хрупкости
     * @return {@link BigDecimal} итоговая стоимость доставки, округлённая до двух знаков после запятой
     * @throws NotFoundException если доставка с указанным идентификатором не найдена
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = findDeliveryOrThrow(orderDto.getDeliveryId());
        Address from = delivery.getFromAddress();
        Address to = delivery.getToAddress();

        BigDecimal cost = BASE_COST;
        log.info("Начало расчёта стоимости доставки для заказа {}. Базовая стоимость: {}", orderDto.getDeliveryId(), cost);

        if (from.getStreet().contains("ADDRESS_2")) {
            cost = cost.add(cost.multiply(WAREHOUSE_2_ADDRESS_MULTIPLIER));
            log.info("Адрес склада содержит ADDRESS_2. Стоимость после увеличения: {}", cost);
        }

        if (orderDto.isFragile()) {
            cost = cost.add(cost.multiply(FRAGILE_MULTIPLIER));
            log.info("Заказ хрупкий. Стоимость после добавления коэффициента хрупкости: {}", cost);
        }

        cost = cost.add(orderDto.getDeliveryWeight().multiply(WEIGHT_MULTIPLIER));
        log.info("Добавляем стоимость веса заказа ({} * {}): {}", orderDto.getDeliveryWeight(), WEIGHT_MULTIPLIER, cost);

        cost = cost.add(orderDto.getDeliveryVolume().multiply(VOLUME_MULTIPLIER));
        log.info("Добавляем стоимость объёма заказа ({} * {}): {}", orderDto.getDeliveryVolume(), VOLUME_MULTIPLIER, cost);

        if (!from.getStreet().equalsIgnoreCase(to.getStreet())) {
            cost = cost.add(cost.multiply(STREET_MULTIPLIER));
            log.info("Улицы склада и доставки различаются. Стоимость после добавления коэффициента улиц: {}", cost);
        }

        cost = cost.setScale(2, RoundingMode.HALF_UP);
        log.info("Итоговая стоимость доставки для заказа {}: {}", orderDto.getDeliveryId(), cost);

        return cost;

    }

    private Delivery findDeliveryOrThrow(UUID deliveryId) {
        return repository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка с id: " + deliveryId + " не найдена"));
    }
}