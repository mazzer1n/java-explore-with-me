package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@AllArgsConstructor
public class BaseClient {
    protected final RestTemplate rest;
    protected final ExceptionHandler exceptionHandler;

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(
        HttpMethod method,
        String path,
        @Nullable Map<String, Object> parameters,
        @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, null);
        ResponseEntity<Object> statsServiceResponse = exceptionHandler.handleStatsServiceException(
            requestEntity,
            method,
            path,
            parameters
        );

        return prepareStatsServiceResponse(statsServiceResponse);
    }

    private static ResponseEntity<Object> prepareStatsServiceResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}