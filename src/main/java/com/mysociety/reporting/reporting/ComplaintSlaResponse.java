package com.mysociety.reporting.reporting;

import java.time.Instant;
import java.util.UUID;

public record ComplaintSlaResponse(UUID id, String complaintNumber, String priority, String status,
                                   Instant slaDueAt, String slaStatus) {
}
