package com.coderiverside.quicknote.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException exception, WebRequest request) {
                log.warn("Resource not found for request {}: {}", request.getDescription(false),
                                exception.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                request.getDescription(false),
                                exception.getMessage(),
                                HttpStatus.NOT_FOUND.value(),
                                LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(
                        BadRequestException exception,
                        WebRequest request) {

                log.warn("Bad request for {}: {}", request.getDescription(false), exception.getMessage());
                ErrorResponse errorResponse = new ErrorResponse(
                                request.getDescription(false),
                                exception.getMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now());

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception exception,
                        WebRequest request) {

                log.error("Unhandled exception for request {}", request.getDescription(false), exception);
                ErrorResponse errorResponse = new ErrorResponse(
                                request.getDescription(false),
                                "An unexpected error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                LocalDateTime.now());

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
