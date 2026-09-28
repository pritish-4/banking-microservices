package com.banking.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String type;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime transactionTime;
    private LocalDateTime receivedAt;
}
