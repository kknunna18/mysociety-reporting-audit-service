# Reporting & Audit Database Scripts

`migrations/V1__reporting_audit_schema.sql` is the versioned PostgreSQL 16+ schema for this service. Apply it with:

```powershell
psql -v ON_ERROR_STOP=1 -U postgres -d mysociety -f database\migrations\V1__reporting_audit_schema.sql
```

The script creates the `mysociety` schema and `pgcrypto` extension idempotently. It does not add a migration
runner or change Hibernate's `ddl-auto=validate` configuration.

## Service-owned objects

The migration is extracted from the authoritative MySociety PostgreSQL DDL and creates:

* `audit_events`, including its canonical validation constraints, access indexes, and immutable trigger.
* `export_requests`, including its canonical export-status constraint.
* `idempotency_records`, including the canonical processing-state constraint, uniqueness key, and expiry index.

`audit_events` is append-only: updates and deletes raise an exception through
`mysociety.prevent_update_delete()`. Audit producers must insert a compensating event instead of modifying history.

## Cross-service identifiers

`society_id`, `actor_user_id`, and `requested_by` remain UUID columns but deliberately have no foreign keys.
Their target records are owned by Society and Identity services. Validate those identifiers through authenticated
service APIs and trusted domain events before writing records; tenant-scoped readers must continue taking the
society UUID only from the authenticated context.

## Reporting projections

The canonical DDL defines `v_unit_balances`, `v_collection_summary_monthly`, `v_complaint_sla_status`, and
`v_current_visitors`, but each view depends on billing, complaint, or visitor tables outside this service boundary.
This migration does not recreate those source tables or invent a replacement projection schema. Provision the
canonical views alongside their owning source tables, or provide an equivalent externally managed reporting
projection before enabling endpoints that query them.
