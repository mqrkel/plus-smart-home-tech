package ru.yandex.practicum.analyzer.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensors")
public class Sensor {
    @Id
    String id;

    @Column(name = "hub_id")
    String hubId;
}
