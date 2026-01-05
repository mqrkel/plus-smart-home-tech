package ru.yandex.practicum.interaction.api.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageDto {
    private List<ProductDto> content;
    private int page;
    private int size;
    private List<SortOrderDto> sort;
}
