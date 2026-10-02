package com.mysociety.reporting.reporting;

import com.mysociety.reporting.security.TenantContextHolder;

import org.springframework.stereotype.Service;

@Service
public class DashboardSummaryService {
    private final DashboardSummaryDataSource dataSource;
    private final TenantContextHolder tenants;

    public DashboardSummaryService(DashboardSummaryDataSource dataSource, TenantContextHolder tenants) {
        this.dataSource = dataSource;
        this.tenants = tenants;
    }

    public DashboardSummaryResponse summary() {
        return dataSource.loadForSociety(tenants.current().societyId());
    }
}
