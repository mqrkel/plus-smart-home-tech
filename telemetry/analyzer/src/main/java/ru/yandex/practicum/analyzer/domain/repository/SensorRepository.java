package ru.yandex.practicum.analyzer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.domain.model.Sensor;

import java.util.List;
import java.util.Set;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    List<Sensor> findAllByHubIdAndIdIn(String hubId, Set<String> ids);

    long countByHubIdAndIdIn(String hubId, Set<String> ids);

    void deleteByHubIdAndId(String hubId, String name);
}
