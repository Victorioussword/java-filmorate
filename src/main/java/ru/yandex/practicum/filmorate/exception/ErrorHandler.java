package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundHandler(final NotFoundException e) {
        log.warn("404", e);
        return Map.of("404 {}", e.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(final ValidationException e) {
        log.warn("400", e);
        return Map.of("400 {}", e.toString());
    }

    @ExceptionHandler
    public ResponseEntity<String> negativeTopQuantityHandler(ConstraintViolationException ex){
        log.warn("400", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidExceptionHandler(final Throwable e) {
        log.warn("400", e);
        return Map.of("400 {}", e.toString());

    }

}
