package com.mysociety.reporting.reporting;

import java.util.UUID;

public interface DashboardSummaryDataSource {
    DashboardSummaryResponse loadForSociety(UUID societyId);
}
