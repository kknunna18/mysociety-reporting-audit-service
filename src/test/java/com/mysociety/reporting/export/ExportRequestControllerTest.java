package com.mysociety.reporting.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.mysociety.reporting.config.SecurityConfig;
import com.mysociety.reporting.security.TenantJwtAuthenticationConverter;
import com.mysociety.reporting.web.ApiExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExportRequestController.class)
@Import(ApiExceptionHandler.class)
class ExportRequestControllerTest {
    @Autowired MockMvc mvc;
    @MockBean ExportRequestService service;

    @Test
    void rejects_invalid_export_request() throws Exception {
        mvc.perform(post("/exports").with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(() -> "PERMISSION_REPORT_EXPORT"))
                .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creates_export_asynchronously() throws Exception {
        when(service.create(any())).thenReturn(new ExportRequestResponse(UUID.randomUUID(), "AUDIT", ExportStatus.PENDING,
                Instant.now(), null, null, false));
        mvc.perform(post("/exports").with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(() -> "PERMISSION_REPORT_EXPORT"))
                .contentType("application/json").content("{\"exportType\":\"AUDIT\"}"))
                .andExpect(status().isAccepted());
    }
}
