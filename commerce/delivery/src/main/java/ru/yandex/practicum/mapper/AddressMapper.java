package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    Address mapToAddress(AddressDto dto);

    AddressDto mapToAddressDto(Address address);
}
