package com.sadcodes.employee.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MissingParameterException extends RuntimeException {
    private String message;
    private HttpStatus status;

    public MissingParameterException(String message) {
        this.message = message;
        status = HttpStatus.BAD_REQUEST;
    }
}
