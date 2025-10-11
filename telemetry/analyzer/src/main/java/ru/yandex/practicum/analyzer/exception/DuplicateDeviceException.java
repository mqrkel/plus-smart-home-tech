package ru.yandex.practicum.analyzer.exception;

public class DuplicateDeviceException extends RuntimeException {
    public DuplicateDeviceException(String name, String hubId) {
        super(String.format("Device %s already exists in hub %s", name, hubId));
    }
}
