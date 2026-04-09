package com.sadcodes.apigateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;

    public BadRequestException(String message) {
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }


    public BadRequestException(String message,HttpStatus status) {
        this.message = message;
        this.httpStatus = status;
    }

//    @Override
//    public String getMessage() {
//        return message;
//    }
//
//    public HttpStatus getHttpStatus() {
//        return httpStatus;
//    }
}
