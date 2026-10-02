package com.mysociety.reporting.reporting;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long residents,
        long openComplaints,
        BigDecimal duesAmount,
        BigDecimal collectedThisMonth,
        long visitorsToday,
        long upcomingBookings) {
}
