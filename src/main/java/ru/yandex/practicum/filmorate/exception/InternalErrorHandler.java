package ru.yandex.practicum.filmorate.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Slf4j
public class InternalErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> processExceptionHandler(final ProcessException e) {
        log.warn("500", e);
        return Map.of("500 {}", e.toString());
    }
}
