package com.banking.apigateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, String>>> authFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Auth service is currently unavailable. Please try again later.")));
    }

    @RequestMapping("/fallback/account")
    public Mono<ResponseEntity<Map<String, String>>> accountFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Account service is currently unavailable. Please try again later.")));
    }

    @RequestMapping("/fallback/transaction")
    public Mono<ResponseEntity<Map<String, String>>> transactionFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Transaction service is currently unavailable. Please try again later.")));
    }
}
