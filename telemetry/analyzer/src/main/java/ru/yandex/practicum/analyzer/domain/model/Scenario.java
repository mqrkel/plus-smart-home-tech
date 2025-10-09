package ru.yandex.practicum.analyzer.domain.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id")
    @NotBlank(message = "Hub ID is required")
    private String hubId;

    @Column(name = "name")
    @NotBlank(message = "Name is required")
    private String name;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ScenarioCondition> scenarioConditions = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ScenarioAction> scenarioActions = new ArrayList<>();
}
