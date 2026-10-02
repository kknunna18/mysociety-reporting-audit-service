package com.mysociety.reporting.reporting;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasAuthority('PERMISSION_REPORT_VIEW')")
public class ReportingController {
    private final ReportingQueryService service;

    public ReportingController(ReportingQueryService service) {
        this.service = service;
    }

    @GetMapping("/unit-balances")
    public List<UnitBalanceResponse> unitBalances() {
        return service.unitBalances();
    }

    @GetMapping("/collections/monthly")
    public List<CollectionSummaryResponse> collections() {
        return service.collections();
    }

    @GetMapping("/complaints/sla")
    public List<ComplaintSlaResponse> complaintSla() {
        return service.complaintSla();
    }

    @GetMapping("/visitors/current")
    public List<CurrentVisitorResponse> currentVisitors() {
        return service.currentVisitors();
    }
}
