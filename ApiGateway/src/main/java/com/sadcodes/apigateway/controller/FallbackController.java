package com.sadcodes.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/employeeServiceFallback")
    public Mono<String>employeeServiceFallbackMethod(){
        return Mono.just("Employee Service is down please try again later");
    }

    @GetMapping("/addressServiceFallback")
    public Mono<String>addressServiceFallbackMethod(){
        return Mono.just("Address Service is down please try again later");
    }


}
