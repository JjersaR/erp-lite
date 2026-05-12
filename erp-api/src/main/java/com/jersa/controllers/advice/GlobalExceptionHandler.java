package com.jersa.controllers.advice;

import com.jersa.dtos.RErrorResponse;
import com.jersa.exceptions.CommandException;
import com.jersa.exceptions.MyBussinessException;
import com.jersa.exceptions.QueryException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // HTTP STATUS 409
    @ExceptionHandler(MyBussinessException.class)
    public ResponseEntity<RErrorResponse> handleBusinesException(MyBussinessException mbe, HttpServletRequest request) {
        log.warn("Rule MyBussinessException detected");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(RErrorResponse.of(
                HttpStatus.CONFLICT,
                "Business rule violation",
                mbe.getMessage(),
                request.getRequestURI()
        ));
    }

    // HTTP STATUS 422
    @ExceptionHandler(CommandException.class)
    public ResponseEntity<RErrorResponse> handleCommandException(CommandException ce, HttpServletRequest request) {
        log.warn("Rule CommandException detected");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(RErrorResponse.of(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Command rule violation",
                ce.getMessage(),
                request.getRequestURI()
        ));
    }

    // HTTP STATUS 404 - 500
    @ExceptionHandler(QueryException.class)
    public ResponseEntity<RErrorResponse> handleCommandException(QueryException qe, HttpServletRequest request) {
        log.warn("Rule QueryException detected");
        boolean isInfraFailure = qe.getCause() instanceof RuntimeException;
        if (isInfraFailure) {
            log.error("Rule QueryException by infrastructure detected");
            return getResponse500(request.getRequestURI());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RErrorResponse.of(
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    qe.getMessage(),
                    request.getRequestURI()
            ));
        }
    }

    // HTTP STATUS 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RErrorResponse> handleRuntimeException(HttpServletRequest request) {
        log.warn("General error detected");
        return getResponse500(request.getRequestURI());
    }

    // HTTP STATUS 500
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = manve.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                (existingValue, value) -> value
        ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    private ResponseEntity<RErrorResponse> getResponse500(String url) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal error occurred",
                "Unexpected error occurred",
                url
        ));
    }
}