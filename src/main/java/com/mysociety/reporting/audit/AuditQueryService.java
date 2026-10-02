package com.mysociety.reporting.audit;

import com.mysociety.reporting.security.TenantContextHolder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AuditQueryService {
    private final AuditEventRepository repository;
    private final AuditEventMapper mapper;
    private final TenantContextHolder tenants;

    public AuditQueryService(AuditEventRepository repository, AuditEventMapper mapper, TenantContextHolder tenants) {
        this.repository = repository;
        this.mapper = mapper;
        this.tenants = tenants;
    }

    public Page<AuditEventResponse> query(String action, String module, String outcome, UUID entityId,
                                          Instant from, Instant to, Pageable pageable) {
        UUID tenantId = tenants.current().societyId();
        Specification<AuditEvent> filter = (root, query, cb) -> cb.equal(root.get("societyId"), tenantId);
        if (action != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("action"), action));
        if (module != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("moduleName"), module));
        if (outcome != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("outcome"), outcome));
        if (entityId != null) filter = filter.and((root, query, cb) -> cb.equal(root.get("entityId"), entityId));
        if (from != null)
            filter = filter.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
        if (to != null) filter = filter.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("occurredAt"), to));
        return repository.findAll(filter, pageable).map(mapper::toResponse);
    }
}
