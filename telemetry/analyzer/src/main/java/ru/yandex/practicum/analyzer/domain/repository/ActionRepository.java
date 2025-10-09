package ru.yandex.practicum.analyzer.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.domain.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
