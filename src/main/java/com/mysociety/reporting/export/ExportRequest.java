package com.mysociety.reporting.export;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "export_requests", schema = "mysociety")
public class ExportRequest {
    @Id private UUID id;
    @Column(name = "society_id", nullable = false) private UUID societyId;
    @Column(name = "requested_by", nullable = false) private UUID requestedBy;
    @Column(name = "export_type", nullable = false) private String exportType;
    @JdbcTypeCode(SqlTypes.JSON) private java.util.Map<String, Object> parameters;
    @Enumerated(EnumType.STRING) private ExportStatus status;
    @Column(name = "object_key") private String objectKey;
    @Column(name = "expires_at") private Instant expiresAt;
    @Column(name = "error_message") private String errorMessage;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "completed_at") private Instant completedAt;

    protected ExportRequest() { }
    public ExportRequest(UUID id, UUID societyId, UUID requestedBy, String exportType, java.util.Map<String, Object> parameters) {
        this.id = id; this.societyId = societyId; this.requestedBy = requestedBy; this.exportType = exportType;
        this.parameters = parameters; this.status = ExportStatus.PENDING; this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getSocietyId() { return societyId; }
    public String getExportType() { return exportType; }
    public ExportStatus getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getCompletedAt() { return completedAt; }
    public boolean isDownloadAvailable() { return status == ExportStatus.COMPLETED && objectKey != null && (expiresAt == null || expiresAt.isAfter(Instant.now())); }
}
