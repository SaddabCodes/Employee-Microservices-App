package com.sadcodes.employee.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>handleResourceNotFoundException(ResourceNotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),ex.getHttpStatus());
        return new ResponseEntity<>(errorResponse,ex.getHttpStatus());

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse>handleBadRequestException(BadRequestException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),ex.getHttpStatus());
        return new ResponseEntity<>(errorResponse,ex.getHttpStatus());

    }

    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ErrorResponse>handleMissingParameterException(MissingParameterException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),ex.getStatus());
        return new ResponseEntity<>(errorResponse,ex.getStatus());

    }




}
