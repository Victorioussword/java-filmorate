package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
@Slf4j   // добавлен placeholder
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundHandler(final NotFoundException e) {
        log.warn("404 {}", e.toString());
        return Map.of("404 {}", e.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidExceptionHandler(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.toString());
        return Map.of("400 {}", e.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> throwableHandler(final Throwable e) {
        log.warn("500 {}", e.toString());
        return Map.of("500 {}", e.toString());
    }
}
