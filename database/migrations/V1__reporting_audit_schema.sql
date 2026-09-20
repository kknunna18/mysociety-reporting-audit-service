/*
 * Reporting & Audit Service schema
 *
 * Source: https://raw.githubusercontent.com/kknunna18/identity-service/main/mysociety_postgresql_complete.sql
 * Scope: service-owned tables only. Cross-service UUIDs intentionally have no foreign keys.
 */

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS mysociety;
SET search_path TO mysociety, public;

CREATE OR REPLACE FUNCTION mysociety.prevent_update_delete()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION '% records are append-only', TG_TABLE_NAME;
END;
$$;

CREATE TABLE IF NOT EXISTS mysociety.audit_events (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id        UUID,
    actor_user_id     UUID,
    actor_type        VARCHAR(20) NOT NULL DEFAULT 'USER'
                      CHECK (actor_type IN ('USER','SERVICE','SYSTEM','SUPPORT')),
    action            VARCHAR(100) NOT NULL,
    module_name       VARCHAR(60) NOT NULL,
    entity_type       VARCHAR(60),
    entity_id         UUID,
    outcome           VARCHAR(20) NOT NULL CHECK (outcome IN ('SUCCESS','FAILURE','DENIED')),
    correlation_id    VARCHAR(100),
    ip_address        INET,
    user_agent        VARCHAR(500),
    old_values        JSONB,
    new_values        JSONB,
    metadata          JSONB,
    occurred_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_audit_tenant_time
    ON mysociety.audit_events (society_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_audit_entity
    ON mysociety.audit_events (society_id, entity_type, entity_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_audit_actor
    ON mysociety.audit_events (actor_user_id, occurred_at DESC);

DROP TRIGGER IF EXISTS trg_audit_events_immutable ON mysociety.audit_events;
CREATE TRIGGER trg_audit_events_immutable
BEFORE UPDATE OR DELETE ON mysociety.audit_events
FOR EACH ROW EXECUTE FUNCTION mysociety.prevent_update_delete();

CREATE TABLE IF NOT EXISTS mysociety.idempotency_records (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id       UUID,
    idempotency_key  VARCHAR(180) NOT NULL,
    operation_name   VARCHAR(100) NOT NULL,
    request_hash     VARCHAR(64),
    response_status  INTEGER,
    response_body    JSONB,
    processing_state VARCHAR(20) NOT NULL DEFAULT 'PROCESSING'
                     CHECK (processing_state IN ('PROCESSING','COMPLETED','FAILED')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at       TIMESTAMPTZ NOT NULL,
    UNIQUE (society_id, operation_name, idempotency_key)
);

CREATE INDEX IF NOT EXISTS ix_idempotency_expiry
    ON mysociety.idempotency_records (expires_at);

CREATE TABLE IF NOT EXISTS mysociety.export_requests (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    society_id      UUID NOT NULL,
    requested_by    UUID NOT NULL,
    export_type     VARCHAR(60) NOT NULL,
    parameters      JSONB,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING','PROCESSING','COMPLETED','FAILED','EXPIRED')),
    object_key      VARCHAR(500),
    expires_at      TIMESTAMPTZ,
    error_message   VARCHAR(1000),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMPTZ
);
