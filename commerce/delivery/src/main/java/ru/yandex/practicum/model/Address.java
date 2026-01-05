package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotBlank
    @Column(name = "country", nullable = false)
    String country;

    @NotBlank
    @Column(name = "city", nullable = false)
    String city;

    @NotBlank
    @Column(name = "street", nullable = false)
    String street;

    @NotBlank
    @Column(name = "house", nullable = false)
    String house;

    @Column(name = "flat")
    String flat;

    public static Address createAddress(String value) {
        return Address.builder()
                .country(value)
                .city(value)
                .street(value)
                .house(value)
                .flat(value)
                .build();
    }
}
