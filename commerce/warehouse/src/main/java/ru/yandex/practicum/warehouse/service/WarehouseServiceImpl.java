package ru.yandex.practicum.warehouse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction.api.warehouse.*;
import ru.yandex.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.yandex.practicum.warehouse.model.Booking;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.BookingRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductMapper warehouseProductMapper;
    private final BookingRepository bookingRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository,
                                WarehouseProductMapper warehouseProductMapper,
                                BookingRepository bookingRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseProductMapper = warehouseProductMapper;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public void addQuantityProduct(AddProductToWarehouseRequest addRequest) {
        WarehouseProduct product = warehouseRepository.findById(addRequest.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException(
                        String.format("Товара с ID = %s нет на складе",
                                addRequest.getProductId()), "Товар не найден на складе"));
        product.setQuantity(product.getQuantity() + addRequest.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country("ADDRESS_1")
                .city("ADDRESS_1")
                .street("ADDRESS_1")
                .house("ADDRESS_1")
                .flat("ADDRESS_1")
                .build();
    }


    @Override
    @Transactional
    public void shippedToDelivery(ShipperToDeliveryRequest request) {
        UUID orderId = request.getOrderId();
        UUID deliveryId = request.getDeliveryId();

        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Не найдена бронь для orderId: " + orderId,
                        "Бронь не найдена на складе"
                ));
        booking.setDeliveryId(deliveryId);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> productsInWarehouse = getProductsOrThrow(products.keySet());
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantityToReturn = entry.getValue();

            WarehouseProduct product = productsInWarehouse.get(productId);
            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException(
                        "Нет информации о товаре на складе id: " + productId,
                        "Товар с указанным ID не найден на складе"
                );
            }

            product.setQuantity(product.getQuantity() + quantityToReturn);
        }
        warehouseRepository.saveAll(productsInWarehouse.values());
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        UUID orderId = request.getOrderId();
        Map<UUID, Long> requestedProducts = request.getProducts();

        Map<UUID, WarehouseProduct> warehouseProducts = getProductsOrThrow(requestedProducts.keySet());

        for (Map.Entry<UUID, Long> entry : requestedProducts.entrySet()) {
            WarehouseProduct product = warehouseProducts.get(entry.getKey());
            if (product == null || product.getQuantity() < entry.getValue()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара ID: " + entry.getKey(),
                        "Недостаточно товара на складе"
                );
            }
            product.setQuantity(product.getQuantity() - entry.getValue());
        }
        warehouseRepository.saveAll(warehouseProducts.values());

        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : requestedProducts.entrySet()) {
            WarehouseProduct product = warehouseProducts.get(entry.getKey());
            BigDecimal q = BigDecimal.valueOf(entry.getValue());

            Dimension d = product.getDimension();
            BigDecimal weight = BigDecimal.valueOf(product.getWeight()).multiply(q);
            BigDecimal volume = BigDecimal.valueOf(d.getWidth())
                    .multiply(BigDecimal.valueOf(d.getHeight()))
                    .multiply(BigDecimal.valueOf(d.getDepth()))
                    .multiply(q);

            totalWeight = totalWeight.add(weight);
            totalVolume = totalVolume.add(volume);
            fragile = fragile || Boolean.TRUE.equals(product.getFragile());
        }

        Booking booking = Booking.builder()
                .orderId(orderId)
                .products(requestedProducts)
                .build();
        bookingRepository.save(booking);

        log.info("Создана бронь заказа {}: {}", orderId, booking.getBookingId());

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight.doubleValue())
                .deliveryVolume(totalVolume.doubleValue())
                .fragile(fragile)
                .build();
    }

    @Override
    @Transactional
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        if (warehouseRepository.existsById(newRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с ID = " + newRequest.getProductId() +
                    " уже зарегистрирован.", "Товар уже существует на складе");
        }
        WarehouseProduct product = warehouseProductMapper.mapToWarProduct(newRequest);
        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto) {

        Map<UUID, WarehouseProduct> productById = warehouseRepository.findAllAsMapByIds(
                shoppingCartDto.getProducts().keySet());

        List<ProductNotEnough> productsNotEnough = new ArrayList<>();
        List<UUID> productsNotFound = new ArrayList<>();

        BookedProductsDto result = shoppingCartDto.getProducts().entrySet().stream().map(entry ->
                        checkSingleProduct(entry, productById, productsNotFound, productsNotEnough))
                .filter(Objects::nonNull).reduce(BookedProductsDto.builder().deliveryVolume(0.0)
                        .deliveryWeight(0.0).fragile(false).build(), this::accumulate);

        if (!productsNotFound.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Нет информации о товарах на складе ID: " +
                    productsNotFound, "Товары не найдены на складе");
        }

        if (!productsNotEnough.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товаров на складе: " +
                    productsNotEnough, "Недостаточно товаров для заказа");
        }

        return result;
    }

    private BookedProductsDto checkSingleProduct(Map.Entry<UUID, Integer> entry, Map<UUID,
            WarehouseProduct> productById, List<UUID> productsNotFound, List<ProductNotEnough> productsNotEnough) {
        UUID id = entry.getKey();
        int wantedCount = entry.getValue();

        WarehouseProduct product = productById.get(id);
        if (product == null) {
            productsNotFound.add(id);
            return null;
        }

        if (wantedCount > product.getQuantity()) {
            productsNotEnough.add(new ProductNotEnough(
                    id,
                    product.getQuantity(),
                    (long) wantedCount
            ));
            return null;
        }

        double volume = Optional.ofNullable(product.getDimension()).map(d -> d.getHeight() * d.getWidth() *
                d.getDepth()).orElse(0.0);

        double weight = product.getWeight() * wantedCount;
        boolean fragile = Boolean.TRUE.equals(product.getFragile());

        return BookedProductsDto.builder().deliveryVolume(volume).deliveryWeight(weight).fragile(fragile).build();
    }

    private BookedProductsDto accumulate(BookedProductsDto acc, BookedProductsDto dto) {
        acc.setDeliveryVolume(acc.getDeliveryVolume() + dto.getDeliveryVolume());
        acc.setDeliveryWeight(acc.getDeliveryWeight() + dto.getDeliveryWeight());
        if (dto.getFragile()) acc.setFragile(true);
        return acc;
    }

    private Map<UUID, WarehouseProduct> getProductsOrThrow(Set<UUID> productIds) {
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        if (products.size() != productIds.size()) {
            throw new NoSpecifiedProductInWarehouseException(
                    "Некоторые товары не найдены на складе",
                    "Один или несколько товаров из заказа отсутствуют на складе"
            );

        }
        return products;
    }
}
