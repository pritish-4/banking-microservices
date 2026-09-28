package com.banking.notification.listener;

import com.banking.notification.event.auth.AuthEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthEventListener {

    private static final Logger smsLog = LoggerFactory.getLogger("SMS_LOGGER");

    @KafkaListener(topics = "${app.kafka.topic.auth-events}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "authKafkaListenerContainerFactory")
    public void onAuthEvent(AuthEvent event) {
        log.info("Received auth event: {} for userId: {}", event.eventType(), event.userId());
        simulateSms(event);
    }

    private void simulateSms(AuthEvent event) {
        String message = switch (event.eventType()) {
            case "PASSWORD_CHANGED" ->
                "[SMS] Hi %s, your account password was recently changed. If this wasn't you, contact support immediately."
                    .formatted(event.username());
            case "EMAIL_CHANGED" ->
                "[SMS] Hi %s, your account email address has been updated. If this wasn't you, contact support immediately."
                    .formatted(event.username());
            default ->
                "[SMS] Hi %s, a security change was made to your account."
                    .formatted(event.username());
        };
        smsLog.info(message);
    }
}
