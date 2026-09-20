package com.mysociety.reporting.audit;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditEventMapper {
    AuditEventResponse toResponse(AuditEvent event);
}
