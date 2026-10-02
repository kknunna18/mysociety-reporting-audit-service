package com.mysociety.reporting.events;

public interface ProjectionUpdater {
    boolean supports(String eventType, int version);

    void apply(VersionedEventEnvelope event);
}
