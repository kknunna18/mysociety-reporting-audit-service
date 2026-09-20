package com.mysociety.reporting.export;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record CreateExportRequest(@NotBlank @Size(max = 60) String exportType, Map<String, Object> parameters) {
}
