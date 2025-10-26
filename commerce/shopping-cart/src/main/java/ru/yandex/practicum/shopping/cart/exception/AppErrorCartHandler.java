package ru.yandex.practicum.shopping.cart.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.interaction.api.exception.AppError;
import ru.yandex.practicum.interaction.api.exception.cart.CartNotFoundException;
import ru.yandex.practicum.interaction.api.exception.cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction.api.exception.cart.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.exception.cart.ShoppingCartDeactivateException;

@Slf4j
@RestControllerAdvice
public class AppErrorCartHandler {

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleNoProductsInShoppingCart(NoProductsInShoppingCartException exp) {
        log.warn(exp.getMessage(), exp);
        return AppError.builder()
                .message("No products found in shopping cart")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AppError handleNotAuthorizedUser(NotAuthorizedUserException exp) {
        log.warn(exp.getMessage(), exp);
        return AppError.builder()
                .message("User is not authorized")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError handleCartNotFound(CartNotFoundException exp) {
        log.warn(exp.getMessage(), exp);
        return AppError.builder()
                .message("Shopping cart not found")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(ShoppingCartDeactivateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleShoppingCartDeactivate(ShoppingCartDeactivateException exp) {
        log.warn(exp.getMessage(), exp);
        return AppError.builder()
                .message("Shopping cart is deactivated")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationException(MethodArgumentNotValidException exp) {
        log.error("Validation error in shopping cart", exp);
        return AppError.builder()
                .message("Validation failed")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleConstraintViolations(ConstraintViolationException exp) {
        log.error("Constraint violation in shopping cart", exp);
        return AppError.builder()
                .message("Constraint violation")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleThrowable(Throwable exp) {
        log.error("Unexpected error in shopping cart", exp);
        return AppError.builder()
                .message("Internal server error")
                .userMessage(exp.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}