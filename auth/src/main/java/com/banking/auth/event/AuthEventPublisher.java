package com.banking.auth.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    @Value("${app.kafka.topic.auth-events}")
    private String topic;

    public void publish(AuthEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.userId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish auth event: {} for userId: {}", event.eventType(), event.userId(), ex);
                    } else {
                        log.info("Published auth event: {} for userId: {}", event.eventType(), event.userId());
                    }
                });
    }
}
