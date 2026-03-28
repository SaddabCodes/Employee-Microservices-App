package com.sadcodes.address.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private String message;
    private HttpStatus httpStatus;

    public ResourceNotFoundException(String message){
        this.message = message;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

}
