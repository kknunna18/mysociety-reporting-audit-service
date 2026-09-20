package com.mysociety.reporting.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TenantJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Optional.ofNullable(jwt.getClaimAsString("society_id"))
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("JWT is missing society_id"));
        List<String> permissions = jwt.getClaimAsStringList("permissions");
        return permissions == null ? List.of() : permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
