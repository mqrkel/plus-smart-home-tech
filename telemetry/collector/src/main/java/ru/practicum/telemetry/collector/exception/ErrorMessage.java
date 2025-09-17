package ru.practicum.telemetry.collector.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorMessage(
        int status,
        String error,
        List<String> details,
        LocalDateTime timestamp
) {
}