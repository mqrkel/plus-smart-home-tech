package ru.yandex.practicum.shopping.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.exception.AppError;
import ru.yandex.practicum.interaction.api.exception.store.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class AppErrorStoreHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError handleProductNotFound(ProductNotFoundException ex) {
        log.warn("Продукт не найден: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("Продукт не найден: " + ex.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidation(MethodArgumentNotValidException ex) {
        log.error("Ошибка валидации данных: {}", ex.getMessage(), ex);
        String validationMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .reduce("", (acc, msg) -> acc + msg + "; ");
        return AppError.builder()
                .message("Ошибка валидации: " + validationMessage)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Нарушение ограничений базы данных: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("Нарушение ограничений базы данных: " + ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleGeneric(Throwable ex) {
        log.error("Необработанная ошибка: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("Внутренняя ошибка сервера: " + ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
