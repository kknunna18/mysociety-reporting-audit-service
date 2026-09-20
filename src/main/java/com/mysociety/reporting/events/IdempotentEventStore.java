package com.mysociety.reporting.events;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IdempotentEventStore {
    private final NamedParameterJdbcTemplate jdbc;
    public IdempotentEventStore(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }
    public boolean claim(VersionedEventEnvelope event) {
        String key = event.eventId().toString();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", java.util.UUID.randomUUID()).addValue("societyId", event.societyId())
                .addValue("key", key).addValue("expiresAt", Instant.now().plus(30, ChronoUnit.DAYS));
        return jdbc.update("""
                INSERT INTO mysociety.idempotency_records
                (id, society_id, idempotency_key, operation_name, processing_state, created_at, expires_at)
                VALUES (:id, :societyId, :key, 'REPORTING_EVENT_CONSUME', 'COMPLETED', CURRENT_TIMESTAMP, :expiresAt)
                ON CONFLICT (society_id, operation_name, idempotency_key) DO NOTHING
                """, parameters) == 1;
    }
}
