package com.mysociety.reporting.events;

import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@ConditionalOnProperty(name = "event.consumer.enabled", havingValue = "true")
public class ReportingEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingEventConsumer.class);
    @Bean
    Consumer<Message<VersionedEventEnvelope>> reportingEventConsumer(IdempotentEventStore eventStore,
                                                                       List<ProjectionUpdater> updaters) {
        return message -> {
            VersionedEventEnvelope event = message.getPayload();
            if (event.eventVersion() < 1) throw new IllegalArgumentException("Event version must be positive");
            if (!eventStore.claim(event)) {
                LOG.info("Ignoring duplicate reporting event eventId={} eventType={}", event.eventId(), event.eventType());
                return;
            }
            updaters.stream().filter(updater -> updater.supports(event.eventType(), event.eventVersion()))
                    .forEach(updater -> updater.apply(event));
            LOG.info("Processed reporting event eventId={} eventType={} version={}", event.eventId(), event.eventType(), event.eventVersion());
        };
    }
}
