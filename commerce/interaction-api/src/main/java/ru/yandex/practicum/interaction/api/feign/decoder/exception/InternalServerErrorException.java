package ru.yandex.practicum.interaction.api.feign.decoder.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
