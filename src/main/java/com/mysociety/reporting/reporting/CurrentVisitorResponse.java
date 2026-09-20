package com.mysociety.reporting.reporting;

import java.time.Instant;
import java.util.UUID;
public record CurrentVisitorResponse(UUID entryId, UUID unitId, Instant checkInAt, UUID checkedInBy) { }
