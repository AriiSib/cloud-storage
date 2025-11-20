package com.khokhlov.cloudstorage.handler;

import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.auth.UsernameAlreadyUsedException;
import com.khokhlov.cloudstorage.exception.minio.StorageDeleteFailedException;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, Object> onException(Exception ex, HttpServletRequest req) {
        log.error("500 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({
            UsernameAlreadyUsedException.class,
            StorageAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> conflict(Exception ex, HttpServletRequest req) {
        log.warn("409 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            AuthenticationCredentialsNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Map<String, String> unauthorized(Exception ex, HttpServletRequest req) {
        log.info("401 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> onInvalidRequestValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("400 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Invalid request");
        return Map.of("message", fieldErrors);
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class,
            MissingServletRequestPartException.class,
            MaxUploadSizeExceededException.class,
            MultipartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> onMultipart(Exception ex, HttpServletRequest req) {
        log.warn("400 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return Map.of("message", ex.getMessage() + ". Maximum file size: 2GB. " +
                "Maximum number of uploaded files: 100");
    }

    @ExceptionHandler(StorageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> onResourceNotFound(Exception ex, HttpServletRequest req) {
        log.warn("404 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(StorageDeleteFailedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, Map<String, String>> onDeleteResourcesNotFound(StorageDeleteFailedException ex, HttpServletRequest req) {
        log.warn("404 {} {} - {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (var missing : ex.getFailures())
            errors.put(missing.object(), missing.message());
        return Map.of("message", errors);
    }

    // Current frontend does not support errors list
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    Map<String, Object> onInvalidRequestValidation(MethodArgumentNotValidException exception) {
//        var fieldErrors = exception.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .collect(Collectors.toMap(
//                        FieldError::getField,
//                        FieldError::getDefaultMessage,
//                        (first, second) -> first
//                ));
//        return Map.of("message", fieldErrors);
//    }

}
