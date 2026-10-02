package com.mysociety.reporting.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record VersionedEventEnvelope(@NotNull UUID eventId, @NotNull UUID societyId, @NotBlank String eventType,
                                     int eventVersion, @NotNull Instant occurredAt, Map<String, Object> payload) {
}
