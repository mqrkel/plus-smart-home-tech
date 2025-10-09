package ru.yandex.practicum.analyzer.exception;

import java.util.Collection;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(Collection<String> badIds) {
        super("Devices not found: " + badIds);
    }
}
