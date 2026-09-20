package com.mysociety.reporting.security;

import java.util.UUID;

public record TenantContext(UUID societyId, UUID userId) {
}
