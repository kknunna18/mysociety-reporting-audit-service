package com.mysociety.reporting.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", schema = "mysociety")
public class AuditEvent {
    @Id
    private UUID id;
    @Column(name = "society_id")
    private UUID societyId;
    @Column(name = "actor_user_id")
    private UUID actorUserId;
    @Column(name = "actor_type")
    private String actorType;
    private String action;
    @Column(name = "module_name")
    private String moduleName;
    @Column(name = "entity_type")
    private String entityType;
    @Column(name = "entity_id")
    private UUID entityId;
    private String outcome;
    @Column(name = "correlation_id")
    private String correlationId;
    @Column(name = "occurred_at")
    private Instant occurredAt;

    protected AuditEvent() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getSocietyId() {
        return societyId;
    }

    public UUID getActorUserId() {
        return actorUserId;
    }

    public String getActorType() {
        return actorType;
    }

    public String getAction() {
        return action;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
