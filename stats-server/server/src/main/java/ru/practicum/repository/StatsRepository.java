package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepository implements StatsRepositoryInterface {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(EndpointHit hit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("stats")
            .usingGeneratedKeyColumns("stats_id");

        simpleJdbcInsert.execute(hit.toMap());
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String query = "SELECT s.app, s.uri, COUNT(s.ip) AS hits FROM stats s " +
            "WHERE s.created BETWEEN ? AND ? ";
        query += addUrisToQuery(uris);
        query += "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC";

        return jdbcTemplate.query(query, (rs, rowNum) -> mapRecordToHit(rs), start, end);
    }

    @Override
    public List<ViewStats> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String query = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) AS hits FROM stats s " +
            "WHERE s.created BETWEEN ? AND ? ";
        query += addUrisToQuery(uris);
        query += "GROUP BY s.app, s.uri, s.ip ORDER BY COUNT(s.ip) DESC";

        return jdbcTemplate.query(query, (rs, rowNum) -> mapRecordToHit(rs), start, end);
    }

    private String addUrisToQuery(List<String> uris) {
        if (uris != null) {
            if (!uris.isEmpty()) {
                return "AND s.uri IN ('" + String.join("', '", uris) + "') ";
            }
        }

        return "";
    }

    private ViewStats mapRecordToHit(ResultSet rs) throws SQLException {
        String app = rs.getString("app");
        String uri = rs.getString("uri");
        long hits = rs.getLong("hits");

        return new ViewStats(app, uri, hits);
    }
}