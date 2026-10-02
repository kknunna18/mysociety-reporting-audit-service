package com.mysociety.reporting.reporting;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ZeroDashboardSummaryDataSource implements DashboardSummaryDataSource {
    @Override
    public DashboardSummaryResponse loadForSociety(UUID societyId) {
        return new DashboardSummaryResponse(0, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
    }
}
