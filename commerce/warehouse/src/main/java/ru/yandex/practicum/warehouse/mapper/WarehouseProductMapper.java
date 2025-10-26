package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {
    WarehouseProduct mapToWarProduct(NewProductInWarehouseRequest request);
}