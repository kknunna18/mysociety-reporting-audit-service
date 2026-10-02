package com.mysociety.reporting.export;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportRequestRepository extends JpaRepository<ExportRequest, UUID> {
    Optional<ExportRequest> findByIdAndSocietyId(UUID id, UUID societyId);
}
