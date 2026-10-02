package com.mysociety.reporting.export;

import com.mysociety.reporting.security.TenantContext;
import com.mysociety.reporting.security.TenantContextHolder;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExportRequestService {
    private final ExportRequestRepository repository;
    private final TenantContextHolder tenants;

    public ExportRequestService(ExportRequestRepository repository, TenantContextHolder tenants) {
        this.repository = repository;
        this.tenants = tenants;
    }

    @Transactional
    public ExportRequestResponse create(CreateExportRequest request) {
        TenantContext tenant = tenants.current();
        ExportRequest saved = repository.save(new ExportRequest(UUID.randomUUID(), tenant.societyId(), tenant.userId(),
                request.exportType(), request.parameters() == null ? java.util.Map.of() : request.parameters()));
        return response(saved);
    }

    @Transactional(readOnly = true)
    public ExportRequestResponse status(UUID id) {
        ExportRequest export = repository.findByIdAndSocietyId(id, tenants.current().societyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Export request was not found"));
        return response(export);
    }

    private ExportRequestResponse response(ExportRequest export) {
        return new ExportRequestResponse(export.getId(), export.getExportType(), export.getStatus(), export.getCreatedAt(),
                export.getCompletedAt(), export.getExpiresAt(), export.isDownloadAvailable());
    }
}
