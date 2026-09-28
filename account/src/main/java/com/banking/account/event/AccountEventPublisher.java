package com.banking.account.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher {

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;

    @Value("${app.kafka.topic.account-events}")
    private String topic;

    public void publish(AccountEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.accountId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish account event: {} for accountId: {}", event.eventType(), event.accountId(), ex);
                    } else {
                        log.info("Published account event: {} for accountId: {}", event.eventType(), event.accountId());
                    }
                });
    }
}
