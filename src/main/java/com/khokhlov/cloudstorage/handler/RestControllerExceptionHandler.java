package com.khokhlov.cloudstorage.handler;

import com.khokhlov.cloudstorage.exception.minio.StorageAlreadyExistsException;
import com.khokhlov.cloudstorage.exception.auth.UsernameAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Map<String, Object> onException(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> onUsernameAlreadyUsed(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(StorageAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> onFileAlreadyExists(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Map<String, String> onInvalidLoginOrPassword(BadCredentialsException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> onInvalid(MethodArgumentNotValidException exception) {
        var fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first
                ));
        return Map.of("message", fieldErrors);
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class,
            MissingServletRequestPartException.class,
            MaxUploadSizeExceededException.class,
            MultipartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> onMultipart(Exception exception) {
        return Map.of("message", exception.getMessage());
    }

}
