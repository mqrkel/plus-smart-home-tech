package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.warehouse.DimensionDto;
import ru.yandex.practicum.warehouse.model.Dimension;

@Mapper(componentModel = "spring")
public interface DimensionMapper {
    Dimension mapToDimension(DimensionDto dimensionDto);
}