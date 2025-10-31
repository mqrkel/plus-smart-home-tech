package ru.yandex.practicum.warehouse.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.exception.AppError;
import ru.yandex.practicum.interaction.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

@Slf4j
@RestControllerAdvice
public class AppErrorWarehouseHandler {

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException ex) {
        log.warn("Нет указанных товаров на складе: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("Нет указанных товаров на складе: " + ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleProductInShoppingCartLowQuantity(ProductInShoppingCartLowQuantityInWarehouse ex) {
        log.warn("На складе меньше товара, чем в корзине: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("На складе меньше товара, чем в корзине: " + ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public AppError handleSpecifiedProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException ex) {
        log.warn("Указанный товар уже есть на складе: {}", ex.getMessage(), ex);
        return AppError.builder()
                .message("Указанный товар уже есть на складе: " + ex.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationException(MethodArgumentNotValidException ex) {
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