package ru.yandex.practicum.interaction.api.warehouse;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductNotEnough {
    UUID productId;
    Long availableCount;
    Long wantedCount;
    Long differenceCount;

    public ProductNotEnough(UUID productId, Long availableCount, Long wantedCount) {
        this.productId = productId;
        this.availableCount = availableCount;
        this.wantedCount = wantedCount;
        this.differenceCount = wantedCount - availableCount;
    }
}

