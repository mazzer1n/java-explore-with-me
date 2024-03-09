package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stats")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private Long id;
    @Column
    private String app;
    @Column
    private String uri;
    @Column
    private String ip;
    @Column(name = "created")
    private LocalDateTime timestamp;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("app", app);
        values.put("uri", uri);
        values.put("ip", ip);
        values.put("created", timestamp);

        return values;
    }
}