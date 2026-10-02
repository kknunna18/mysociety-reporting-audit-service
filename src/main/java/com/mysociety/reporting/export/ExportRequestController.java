package com.mysociety.reporting.export;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exports")
public class ExportRequestController {
    private final ExportRequestService service;

    public ExportRequestController(ExportRequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('PERMISSION_REPORT_EXPORT')")
    @Operation(summary = "Queue an asynchronous report export for the authenticated society")
    public ExportRequestResponse create(@Valid @RequestBody CreateExportRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_REPORT_EXPORT')")
    public ExportRequestResponse status(@PathVariable UUID id) {
        return service.status(id);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAuthority('PERMISSION_REPORT_EXPORT')")
    public ExportRequestResponse downloadMetadata(@PathVariable UUID id) {
        return service.status(id);
    }
}
