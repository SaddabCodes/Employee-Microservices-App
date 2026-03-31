package com.sadcodes.employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadcodes.employee.exception.CustomException;
import com.sadcodes.employee.exception.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

     /*   int status = response.status();
        if(status == 503){
            return new BadRequestException("Employee service is down. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE);
        }*/

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try(InputStream is = response.body().asInputStream()) {
            ErrorResponse errorResponse = objectMapper.readValue(is, ErrorResponse.class);
            return new CustomException(errorResponse.getMessage(), errorResponse.getHttpStatus());
        } catch (IOException e) {
            throw new CustomException("INTERNAL_SERVER_ERROR");
        }
    }
}
