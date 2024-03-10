package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.practicum.StatClient.QueryParameters.*;

@Service
public class StatClient extends BaseClient {
    private static final String GET_PREFIX = "/stats";
    private static final String POST_PREFIX = "/hit";

    @Autowired
    public StatClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder, ExceptionHandler exceptionHandler) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build(),
            exceptionHandler
        );
    }

    public ResponseEntity<Object> getViewStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
            START, LocalDateTime.parse(start),
            END, LocalDateTime.parse(end),
            URIS, uris,
            UNIQUE, unique
        );

        return get(GET_PREFIX + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto dto) {
        return post(POST_PREFIX, dto);
    }

    public static class QueryParameters {
        public static final String START = "start";
        public static final String END = "end";
        public static final String URIS = "uris";
        public static final String UNIQUE = "unique";
    }
}