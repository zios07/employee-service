package com.workmotion.employeeservice.controller.advice;

import com.workmotion.employeeservice.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Throwable throwable) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, throwable.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Throwable throwable) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Data
    @AllArgsConstructor
    class ErrorResponse {
        private HttpStatus status;
        private String message;
    }

}
