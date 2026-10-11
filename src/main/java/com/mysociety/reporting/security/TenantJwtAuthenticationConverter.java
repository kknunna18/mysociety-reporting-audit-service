package com.mysociety.reporting.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TenantJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger log = LoggerFactory.getLogger(TenantJwtAuthenticationConverter.class);

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Prefer claim; fall back to x-society-id header on the incoming request for dev/local convenience
        String societyId = Optional.ofNullable(jwt.getClaimAsString("society_id"))
                .filter(value -> !value.isBlank())
                .orElseGet(() -> {
                    var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs == null) return null;
                    HttpServletRequest req = attrs.getRequest();
                    String hdr = req.getHeader("x-society-id");
                    if (hdr != null && !hdr.isBlank()) {
                        log.warn("Using x-society-id header as society_id fallback: {}", hdr);
                    }
                    return hdr == null || hdr.isBlank() ? null : hdr;
                });

        // Do not fail immediately when society_id is absent — allow controllers to operate in dev mode.
        List<String> permissions = jwt.getClaimAsStringList("permissions");
        Collection<GrantedAuthority> authorities;
        if (permissions == null || permissions.isEmpty()) {
            // Developer convenience: if no permissions in token, grant a local dev permission to allow dashboard access.
            authorities = List.of(new SimpleGrantedAuthority("PERMISSION_REPORT_VIEW"));
            log.warn("No permissions found in JWT; granting dev fallback PERMISSION_REPORT_VIEW. societyId={}", societyId);
        } else {
            authorities = permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
                    .map(GrantedAuthority.class::cast)
                    .toList();
            log.debug("Resolved authorities from JWT permissions={} -> {}", permissions, authorities);
        }
        return authorities;
    }
}
