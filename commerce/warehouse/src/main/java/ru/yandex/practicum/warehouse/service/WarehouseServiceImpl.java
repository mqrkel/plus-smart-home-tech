package ru.yandex.practicum.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.*;
import ru.yandex.practicum.interaction.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.AddressMapper;
import ru.yandex.practicum.warehouse.mapper.WarehouseProductMapper;
import ru.yandex.practicum.warehouse.model.Address;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.AddressRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;
    private final AddressMapper addressMapper;
    private final WarehouseProductMapper warehouseProductMapper;
    private final UUID idAddress;

    public WarehouseServiceImpl(AddressRepository addressRepository, WarehouseRepository warehouseRepository, AddressMapper addressMapper, WarehouseProductMapper warehouseProductMapper) {
        this.addressRepository = addressRepository;
        this.warehouseRepository = warehouseRepository;
        this.addressMapper = addressMapper;
        this.warehouseProductMapper = warehouseProductMapper;

        String[] addresses = {"ADDRESS_1", "ADDRESS_2"};
        int randomIdx = new SecureRandom().nextInt(addresses.length);
        this.idAddress = addressRepository.save(Address.createAddress(addresses[randomIdx])).getId();
    }

    @Override
    @Transactional
    public void addQuantityProduct(AddProductToWarehouseRequest addRequest) {
        WarehouseProduct product = warehouseRepository.findById(addRequest.getProductId()).orElseThrow(() -> new NoSpecifiedProductInWarehouseException(String.format("Товара с ID = %s нет на складе", addRequest.getProductId()), "Товар не найден на складе"));
        product.setQuantity(product.getQuantity() + addRequest.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        Address address = addressRepository.findById(idAddress).orElseThrow(() -> new IllegalStateException("Адрес в БД не найден, ID = " + idAddress));
        return addressMapper.mapToAddressDto(address);
    }

    @Override
    @Transactional
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        if (warehouseRepository.existsById(newRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с ID = " + newRequest.getProductId() + " уже зарегистрирован.", "Товар уже существует на складе");
        }
        WarehouseProduct product = warehouseProductMapper.mapToWarProduct(newRequest);
        warehouseRepository.save(product);
    }

    @Override
    public BookedProductsDto checkQuantityProducts(ShoppingCartDto shoppingCartDto) {

        Map<UUID, WarehouseProduct> productById = warehouseRepository.findAllAsMapByIds(shoppingCartDto.getProducts().keySet());

        List<ProductNotEnough> productsNotEnough = new ArrayList<>();
        List<UUID> productsNotFound = new ArrayList<>();

        BookedProductsDto result = shoppingCartDto.getProducts().entrySet().stream().map(entry -> checkSingleProduct(entry, productById, productsNotFound, productsNotEnough)).filter(Objects::nonNull).reduce(BookedProductsDto.builder().deliveryVolume(0.0).deliveryWeight(0.0).fragile(false).build(), this::accumulate);

        if (!productsNotFound.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Нет информации о товарах на складе ID: " + productsNotFound, "Товары не найдены на складе");
        }

        if (!productsNotEnough.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товаров на складе: " + productsNotEnough, "Недостаточно товаров для заказа");
        }

        return result;
    }

    private BookedProductsDto checkSingleProduct(Map.Entry<UUID, Integer> entry, Map<UUID, WarehouseProduct> productById, List<UUID> productsNotFound, List<ProductNotEnough> productsNotEnough) {
        UUID id = entry.getKey();
        int wantedCount = entry.getValue();

        WarehouseProduct product = productById.get(id);
        if (product == null) {
            productsNotFound.add(id);
            return null;
        }

        if (wantedCount > product.getQuantity()) {
            productsNotEnough.add(new ProductNotEnough(id, product.getQuantity(), wantedCount));
            return null;
        }

        double volume = Optional.ofNullable(product.getDimension()).map(d -> d.getHeight() * d.getWidth() * d.getDepth()).orElse(0.0);

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
}
