package com.trademaster.ims.mobile.common.exception;

import com.trademaster.ims.mobile.common.response.MobileApiErrorResponse;
import com.trademaster.ims.mobile.common.response.MobileFieldError;
import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;

@RestControllerAdvice(basePackages = "com.trademaster.ims.mobile")
public class MobileExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MobileExceptionHandler.class);

    @ExceptionHandler(CustomerAuthException.class)
    ResponseEntity<MobileApiErrorResponse> customerAuth(CustomerAuthException ex, HttpServletRequest request) {
        return response(ex.status(), ex.code(), ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MobileResourceNotFoundException.class)
    ResponseEntity<MobileApiErrorResponse> notFound(MobileResourceNotFoundException ex, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MobileValidationException.class)
    ResponseEntity<MobileApiErrorResponse> validation(MobileValidationException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), ex.getFieldErrors(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<MobileApiErrorResponse> constraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<MobileFieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> new MobileFieldError(lastNode(v.getPropertyPath().toString()), v.getMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "One or more request values are invalid.", errors, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<MobileApiErrorResponse> unreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "The request body is missing or invalid.", List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<MobileApiErrorResponse> bodyValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<MobileFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new MobileFieldError(e.getField(), e.getDefaultMessage())).toList();
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "One or more request values are invalid.", errors, request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    ResponseEntity<MobileApiErrorResponse> malformed(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "A request parameter is missing or invalid.",
                List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<MobileApiErrorResponse> unexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected mobile catalog failure at {}", request.getRequestURI(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "The request could not be completed.", List.of(), request);
    }

    private ResponseEntity<MobileApiErrorResponse> response(HttpStatus status, String code, String message,
            List<MobileFieldError> errors, HttpServletRequest request) {
        return ResponseEntity.status(status).body(MobileApiErrorResponse.of(code, message, errors, request.getRequestURI()));
    }

    private String lastNode(String path) {
        int index = path.lastIndexOf('.');
        return index >= 0 ? path.substring(index + 1) : path;
    }
}


