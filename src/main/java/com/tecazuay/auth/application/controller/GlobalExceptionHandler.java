package com.tecazuay.auth.application.controller;

import com.tecazuay.auth.application.dto.ErrorResponseDto;
import com.tecazuay.auth.application.exception.InvalidCredentialsException;
import com.tecazuay.auth.application.exception.InvalidTokenException;
import com.tecazuay.auth.application.exception.UserAlreadyExistsException;
import com.tecazuay.auth.application.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            ServerWebExchange exchange) {

        log.error("User already exists: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .error("User Already Exists")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleUserNotFound(
            UserNotFoundException ex,
            ServerWebExchange exchange) {

        log.error("User not found: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .error("User Not Found")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            ServerWebExchange exchange) {

        log.error("Invalid credentials: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("Invalid email or password")
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Invalid Credentials")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleInvalidToken(
            InvalidTokenException ex,
            ServerWebExchange exchange) {

        log.error("Invalid token: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("Invalid or expired token")
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Invalid Token")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidationErrors(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        log.error("Validation error: {}", ex.getMessage());

        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("Validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .details(details)
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler({BadPaddingException.class, IllegalBlockSizeException.class})
    public Mono<ResponseEntity<ErrorResponseDto>> handleEncryptionErrors(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Encryption/Decryption error: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("Invalid encrypted data format")
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Encryption Error")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalArgument(
            IllegalArgumentException ex,
            ServerWebExchange exchange) {

        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("Invalid request parameter")
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected error occurred: ", ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getURI().getPath())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
