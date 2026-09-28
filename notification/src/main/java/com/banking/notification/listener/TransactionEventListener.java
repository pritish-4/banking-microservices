package com.banking.notification.listener;

import com.banking.notification.entity.Notification;
import com.banking.notification.event.TransactionEvent;
import com.banking.notification.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private static final Logger smsLog = LoggerFactory.getLogger("SMS_LOGGER");

    private final NotificationRepo notificationRepo;

    @KafkaListener(topics = "${app.kafka.topic.transaction-events}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "transactionKafkaListenerContainerFactory")
    public void onTransactionEvent(TransactionEvent event) {
        log.info("Received transaction event: {} type={} amount={}", event.transactionId(), event.type(), event.amount());

        Notification notification = new Notification();
        notification.setTransactionId(event.transactionId());
        notification.setType(event.type());
        notification.setSourceAccountId(event.sourceAccountId());
        notification.setTargetAccountId(event.targetAccountId());
        notification.setAmount(event.amount());
        notification.setStatus(event.status());
        notification.setTransactionTime(event.createdAt());
        notification.setReceivedAt(LocalDateTime.now());
        notificationRepo.save(notification);

        simulateSms(event);
        log.info("Notification saved and SMS simulated for transaction: {}", event.transactionId());
    }

    private void simulateSms(TransactionEvent event) {
        String message = switch (event.type()) {
            case "DEPOSIT" ->
                "[SMS] Account %d: A deposit of £%.2f has been credited to your account. Ref: %s"
                    .formatted(event.targetAccountId(), event.amount(), event.transactionId());
            case "WITHDRAW" ->
                "[SMS] Account %d: A withdrawal of £%.2f has been debited from your account. Ref: %s"
                    .formatted(event.sourceAccountId(), event.amount(), event.transactionId());
            case "TRANSFER" ->
                "[SMS] Account %d: A transfer of £%.2f to account %d has been processed. Ref: %s"
                    .formatted(event.sourceAccountId(), event.amount(), event.targetAccountId(), event.transactionId());
            default ->
                "[SMS] Account transaction of £%.2f processed. Ref: %s"
                    .formatted(event.amount(), event.transactionId());
        };
        smsLog.info(message);
    }
}
