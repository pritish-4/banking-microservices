package com.banking.notification.listener;

import com.banking.notification.event.account.AccountEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountEventListener {

    private static final Logger smsLog = LoggerFactory.getLogger("SMS_LOGGER");

    @KafkaListener(topics = "${app.kafka.topic.account-events}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "accountKafkaListenerContainerFactory")
    public void onAccountEvent(AccountEvent event) {
        log.info("Received account event: {} for accountId: {}", event.eventType(), event.accountId());
        simulateSms(event);
    }

    private void simulateSms(AccountEvent event) {
        String message = switch (event.eventType()) {
            case "ACCOUNT_CLOSED" ->
                "[SMS] Account %s has been successfully closed. Thank you for banking with us."
                    .formatted(event.accountNumber());
            case "ACCOUNT_FROZEN" ->
                "[SMS] Account %s has been frozen. Please contact support for further assistance."
                    .formatted(event.accountNumber());
            default ->
                "[SMS] A change has been made to your account %s."
                    .formatted(event.accountNumber());
        };
        smsLog.info(message);
    }
}
