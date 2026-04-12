package com.sadcodes.address.exception;


import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<com.sadcodes.address.exception.ErrorResponse>handleResourceNotFoundException(ResourceNotFoundException ex){
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

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse>handleCustomException(CustomException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),ex.getStatus());
        return new ResponseEntity<>(errorResponse,ex.getStatus());

    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleRetryableException(RetryableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Employee Service is down. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        HttpStatus finalStatus = status != null ? status : INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), finalStatus);
        return new ResponseEntity<>(errorResponse, finalStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>handleGenericException(Exception ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }


}
