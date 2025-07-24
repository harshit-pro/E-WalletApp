package com.res.server.transactionservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

// TransactionEvent.java
public record TransactionEvent(
        String eventId,
        String transactionId,
        TransactionEventType eventType,
        Instant timestamp,
        String senderId,
        String receiverId,
        BigDecimal amount,
        Map<String, Object> metadata) {
    public enum TransactionEventType {
        INITIATED, PROCESSING, COMPLETED, FAILED, REVERSED
    }
}
