package com.sadcodes.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class Validator {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final List<String> endPoint = List.of(
            "/auth/register-user",
            "/auth/generate-token"
    );

    public Predicate<ServerHttpRequest> predicate = request -> {
        String requestPath = request.getURI().getPath();
        return endPoint.stream().noneMatch(path -> antPathMatcher.match(path, requestPath));
    };
}
