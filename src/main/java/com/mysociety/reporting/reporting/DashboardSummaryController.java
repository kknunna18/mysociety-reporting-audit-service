package com.mysociety.reporting.reporting;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasAuthority('PERMISSION_REPORT_VIEW')")
public class DashboardSummaryController {
    private final DashboardSummaryService service;

    public DashboardSummaryController(DashboardSummaryService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary() {
        return service.summary();
    }
}
