package com.mysociety.reporting.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mysociety.reporting.security.TenantContext;
import com.mysociety.reporting.security.TenantContextHolder;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardSummaryServiceTest {
    @Mock
    DashboardSummaryDataSource dataSource;

    @Mock
    TenantContextHolder tenants;

    @Test
    void scopes_summary_data_to_the_current_society() {
        UUID societyId = UUID.randomUUID();
        DashboardSummaryResponse expected = new DashboardSummaryResponse(17, 3, new BigDecimal("125.50"),
                new BigDecimal("42.25"), 4, 2);
        when(tenants.current()).thenReturn(new TenantContext(societyId, UUID.randomUUID()));
        when(dataSource.loadForSociety(societyId)).thenReturn(expected);

        DashboardSummaryResponse actual = new DashboardSummaryService(dataSource, tenants).summary();

        assertEquals(expected, actual);
        verify(dataSource).loadForSociety(societyId);
    }

    @Test
    void zero_data_source_returns_all_summary_fields_as_zero() {
        DashboardSummaryResponse response = new ZeroDashboardSummaryDataSource()
                .loadForSociety(UUID.randomUUID());

        assertEquals(new DashboardSummaryResponse(0, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0), response);
    }
}
