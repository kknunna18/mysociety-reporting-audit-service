# MySociety Reporting & Audit Service

Spring Boot 3 / Java 21 tenant-scoped reporting and audit read service. It serves `/api/v1` on port `8087`.

## Run locally

1. Copy `.env.example` to `.env` and provide a strong `JWT_HS256_SECRET` (at least 32 characters).
2. Start PostgreSQL with `docker compose up -d postgres`, then apply the authoritative schema from the identity-service
   repository.
3. Run `gradlew.bat bootRun --args="--spring.profiles.active=local"`.

The JWT resource server accepts only HS256 tokens with issuer `mysociety-identity`. A valid `sub` and
`society_id` UUID claim are mandatory; all queries bind the tenant only from that claim. The body never carries
a society identifier.

## API

Swagger UI is at `/api/v1/swagger-ui.html`; OpenAPI is at `/api/v1/openapi`.

* `GET /api/v1/audit-events` filters and pages append-only audit events.
* `GET /api/v1/reports/{unit-balances,collections/monthly,complaints/sla,visitors/current}` reads supplied reporting
  views.
* `GET /api/v1/dashboard/summary` returns six summary metrics for the authenticated society.
* `POST /api/v1/exports` queues an export; `GET /api/v1/exports/{id}` and `/download` expose tenant-safe status/download
  metadata.

Permissions are read from the `permissions` JWT claim: `AUDIT_VIEW`, `REPORT_VIEW`, and `REPORT_EXPORT`.

## Schema boundary and events

The authoritative DDL provides `audit_events`, `export_requests`, `idempotency_records`, and the views
`v_unit_balances`, `v_collection_summary_monthly`, `v_complaint_sla_status`, and `v_current_visitors`.
It does **not** provide separate materialized/reporting projection table DDL. This service deliberately does not
invent such tables: it consumes those views and leaves the versioned Kafka event boundary disabled by default.
When `EVENT_CONSUMER_ENABLED=true`, event IDs are claimed idempotently in the supplied `idempotency_records`
table before a supported projection updater runs. No raw event payloads are logged.

The dashboard summary is intentionally a stub: `ZeroDashboardSummaryDataSource` returns zeros instead of combining
the partial reporting views with all domain data. Implement `DashboardSummaryDataSource` to aggregate tenant-scoped
resident, complaint, billing, visitor, and booking data (including upstream integration for domains not available in
this service); the service passes the authenticated society UUID from the JWT to that interface.

Hibernate runs with `ddl-auto=validate` and schema `mysociety`; it never creates or changes database objects.
Health, readiness/liveness, and Prometheus are exposed through Actuator.
