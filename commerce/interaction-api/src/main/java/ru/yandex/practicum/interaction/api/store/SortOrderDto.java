package ru.yandex.practicum.interaction.api.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortOrderDto {
    private String property;
    private String direction;
}
