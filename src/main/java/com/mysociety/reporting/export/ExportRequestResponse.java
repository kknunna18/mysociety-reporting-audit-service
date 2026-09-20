package com.mysociety.reporting.export;

import java.time.Instant;
import java.util.UUID;

public record ExportRequestResponse(UUID id, String exportType, ExportStatus status, Instant createdAt,
                                    Instant completedAt, Instant expiresAt, boolean downloadAvailable) {
}
