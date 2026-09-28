package com.banking.transaction.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventPublisher {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("${app.kafka.topic.transaction-events}")
    private String topic;

    public void publish(TransactionEvent event) {
        kafkaTemplate.send(topic, event.transactionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish transaction event: {}", event.transactionId(), ex);
                    } else {
                        log.info("Published transaction event: {}", event.transactionId());
                    }
                });
    }
}
