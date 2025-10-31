package ru.yandex.practicum.interaction.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppError {
    private final String message;
    private final String userMessage;
    private final HttpStatus httpStatus;

    public static Builder builder() {
        return new Builder();
    }
}

