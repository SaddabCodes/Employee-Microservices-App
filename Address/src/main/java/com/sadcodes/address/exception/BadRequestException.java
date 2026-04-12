package com.sadcodes.address.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BadRequestException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;

    public BadRequestException(String message) {
        super(message);
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
