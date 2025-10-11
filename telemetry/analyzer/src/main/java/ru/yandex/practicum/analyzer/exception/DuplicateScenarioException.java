package ru.yandex.practicum.analyzer.exception;

public class DuplicateScenarioException extends RuntimeException {
    public DuplicateScenarioException(String name, String hubId) {
        super(String.format("Scenario %s already exists in hub %s", name, hubId));
    }
}
