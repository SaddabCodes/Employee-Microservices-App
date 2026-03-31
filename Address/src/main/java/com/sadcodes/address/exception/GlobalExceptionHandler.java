package com.sadcodes.address.exception;


import feign.FeignException;
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
    public ResponseEntity<ErrorResponse>handleGlobalException(CustomException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),ex.getStatus());
        return new ResponseEntity<>(errorResponse,ex.getStatus());

    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof CustomException customException) {
            return handleGlobalException(customException);
        }
        // Generic fallback for other Feign exceptions
        ErrorResponse errorResponse = new ErrorResponse("Error during microservice communication", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>handleGenericException(Exception ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }


}
