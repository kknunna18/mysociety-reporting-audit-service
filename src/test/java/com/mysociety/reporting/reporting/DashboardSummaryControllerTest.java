package com.mysociety.reporting.reporting;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mysociety.reporting.config.SecurityConfig;
import com.mysociety.reporting.security.TenantJwtAuthenticationConverter;
import com.mysociety.reporting.web.ApiExceptionHandler;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardSummaryController.class)
@Import({SecurityConfig.class, TenantJwtAuthenticationConverter.class, ApiExceptionHandler.class})
class DashboardSummaryControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    DashboardSummaryService service;

    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    void returns_the_expected_summary_for_report_view_permission() throws Exception {
        when(service.summary()).thenReturn(new DashboardSummaryResponse(17, 3, new BigDecimal("125.50"),
                new BigDecimal("42.25"), 4, 2));

        mvc.perform(get("/dashboard/summary")
                        .with(jwt().jwt(token -> token.claim("society_id", UUID.randomUUID().toString()))
                                .authorities(() -> "PERMISSION_REPORT_VIEW")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$.residents").value(17))
                .andExpect(jsonPath("$.openComplaints").value(3))
                .andExpect(jsonPath("$.duesAmount").value(125.50))
                .andExpect(jsonPath("$.collectedThisMonth").value(42.25))
                .andExpect(jsonPath("$.visitorsToday").value(4))
                .andExpect(jsonPath("$.upcomingBookings").value(2));
    }

    @Test
    void denies_requests_without_report_view_permission() throws Exception {
        mvc.perform(get("/dashboard/summary")
                        .with(jwt().jwt(token -> token.claim("society_id", UUID.randomUUID().toString()))))
                .andExpect(status().isForbidden());
    }
}
