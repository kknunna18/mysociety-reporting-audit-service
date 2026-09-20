package com.mysociety.reporting.audit;

import io.swagger.v3.oas.annotations.Operation;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit-events")
public class AuditController {
    private final AuditQueryService service;
    public AuditController(AuditQueryService service) { this.service = service; }

    @Operation(summary = "Query append-only audit events for the authenticated society")
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_AUDIT_VIEW')")
    public Page<AuditEventResponse> query(
            @RequestParam(required = false) String action, @RequestParam(required = false, name = "module") String module,
            @RequestParam(required = false) String outcome, @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 25, sort = "occurredAt") Pageable pageable) {
        return service.query(action, module, outcome, entityId, from, to, pageable);
    }
}
