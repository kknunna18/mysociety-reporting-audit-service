package com.mysociety.reporting.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(UUID id, UUID actorUserId, String actorType, String action, String moduleName,
                                 String entityType, UUID entityId, String outcome, String correlationId,
                                 Instant occurredAt) {
}
