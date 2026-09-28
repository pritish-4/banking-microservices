package com.banking.transaction.client;

import com.banking.transaction.exception.AccountNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractBody(response);
        return switch (response.status()) {
            case 404 -> new AccountNotFoundException(message != null ? message : "Account not found");
            case 400 -> new AccountNotFoundException(message != null ? message : "Bad request to account service");
            default -> new Default().decode(methodKey, response);
        };
    }

    private String extractBody(Response response) {
        try {
            if (response.body() != null) {
                return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}
        return null;
    }
}
