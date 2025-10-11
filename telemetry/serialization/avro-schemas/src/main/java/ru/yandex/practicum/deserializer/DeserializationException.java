package ru.yandex.practicum.deserializer;

public class DeserializationException extends RuntimeException {

    public DeserializationException(String s, Exception e) {
        super(s, e);
    }
}
