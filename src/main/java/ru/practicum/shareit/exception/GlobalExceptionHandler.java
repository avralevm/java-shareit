package ru.practicum.shareit.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors  = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.BAD_REQUEST.value());
        fieldErrors.put("error", "Validation Error");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            log.error("[ERROR] Ошибка валидации полей: {}",  error.getDefaultMessage());
        });
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Map<String, Object> handleMissingHeader(MissingRequestHeaderException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Required header is missing");
        errorResponse.put("message", String.format("Header '%s' is required", ex.getHeaderName()));

        log.error("[ERROR] Отсутствует обязательный заголовок: {}", ex.getHeaderName());

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.NOT_FOUND.value());
        fieldErrors.put("error", "Resource Not Found");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Ресурс не найден: {}", ex.getMessage());
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public Map<String, Object> handleDuplicateException(DuplicateException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.CONFLICT.value());
        fieldErrors.put("error", "Resource already exists");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Попытка дублирования ресурса: {}", ex.getMessage());
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Map<String, Object> handleValidationException(ValidationException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.CONFLICT.value());
        fieldErrors.put("error", "Validation Error");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Ошибка валидации cущностей: {}", ex.getMessage());
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, Object> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.CONFLICT.value());
        fieldErrors.put("error", "BadRequestException Error");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Ошибка операции: {}", ex.getMessage());
        return fieldErrors;
    }
}