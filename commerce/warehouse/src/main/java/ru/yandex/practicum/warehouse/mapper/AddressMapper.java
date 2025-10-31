package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto mapToAddressDto(Address address);

    Address mapToAddress(AddressDto dto);
}