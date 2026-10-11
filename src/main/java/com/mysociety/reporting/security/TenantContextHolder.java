package com.mysociety.reporting.security;

import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContextHolder {
    private final boolean relaxTenancy;

    public TenantContextHolder(Environment env) {
        // Relax tenancy checks in 'local' profile or when reporting.dev.relax-tenancy=true
        this.relaxTenancy = env.acceptsProfiles("local") || Boolean.parseBoolean(env.getProperty("reporting.dev.relax-tenancy", "false"));
    }

    public TenantContext current() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token)) {
            throw new AccessDeniedException("A JWT tenant context is required");
        }
        UUID societyId = parseClaim(token.getToken().getClaimAsString("society_id"), "society_id");
        UUID userId = parseClaim(token.getToken().getSubject(), "sub");
        return new TenantContext(societyId, userId);
    }

    private UUID parseClaim(String value, String claim) {
        try {
            if (value == null) {
                if ("society_id".equals(claim) && relaxTenancy) {
                    // In dev mode, allow missing society_id by returning null
                    return null;
                }
                throw new NullPointerException();
            }
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new AccessDeniedException("JWT is missing a valid " + claim + " claim");
        }
    }
}
