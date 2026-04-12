package com.sadcodes.address.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadcodes.address.exception.CustomException;
import com.sadcodes.address.exception.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        int status = response.status();
        if (status == 503) {
            return new CustomException("Employee Service is down. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        HttpStatus resolvedStatus = HttpStatus.resolve(status);
        HttpStatus finalStatus = resolvedStatus != null ? resolvedStatus : HttpStatus.INTERNAL_SERVER_ERROR;

        if (response.body() == null) {
            return new CustomException("Employee service request failed.", finalStatus);
        }

        try (InputStream is = response.body().asInputStream()) {
            ErrorResponse errorResponse = objectMapper.readValue(is, ErrorResponse.class);
            HttpStatus parsedStatus = errorResponse.getHttpStatus() != null ? errorResponse.getHttpStatus() : finalStatus;
            String message = errorResponse.getMessage() != null ? errorResponse.getMessage() : "Employee service request failed.";
            return new CustomException(message, parsedStatus);
        } catch (IOException | RuntimeException e) {
            return new CustomException("Employee service request failed.", finalStatus);
        }
    }
}
