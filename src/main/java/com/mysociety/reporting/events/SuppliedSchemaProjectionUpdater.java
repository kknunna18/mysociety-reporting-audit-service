package com.mysociety.reporting.events;

import org.springframework.stereotype.Component;

@Component
public class SuppliedSchemaProjectionUpdater implements ProjectionUpdater {
    @Override
    public boolean supports(String eventType, int version) {
        return false;
    }
    @Override
    public void apply(VersionedEventEnvelope event) {
        throw new UnsupportedOperationException("No event-managed projection table is present in the supplied DDL");
    }
}
