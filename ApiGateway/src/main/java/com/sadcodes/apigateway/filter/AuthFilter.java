package com.sadcodes.apigateway.filter;

import com.sadcodes.apigateway.exception.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private Validator validator;
    
    @Autowired
    private JwtUtil jwtUtil;

    public AuthFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.predicate.test(exchange.getRequest())) {
                String authHeader = exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
                try {
                    jwtUtil.validateToken(token);
                } catch (Exception e) {
                    throw new BadRequestException("Invalid Token", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);

        };
    }

    public static class Config {

    }
}
