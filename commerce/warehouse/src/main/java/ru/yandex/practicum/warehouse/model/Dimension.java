package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Entity
@Table(name = "dimension")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    @Positive
    @Column(nullable = false)
    Double width;

    @NotNull
    @Positive
    @Column(nullable = false)
    Double height;

    @NotNull
    @Positive
    @Column(nullable = false)
    Double depth;
}
