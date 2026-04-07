package com.sadcodes.auth.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        // FIX: Assign the passed parameter to the class field
        this.status = status;
    }

    public CustomException(String message) {
        super(message);
        // FIX: Set a default status if only a message is provided
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}