package com.res.server.transactionservice.model;


import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String externalTransactionId;

    private String senderId;

    private String receiverId;

    private Long amount;

    private String reason;

    @Enumerated(value=EnumType.STRING) // String will be stored in the database
    private TransactionStatus status;

    @CreationTimestamp
    private Date createdOn;
    @UpdateTimestamp
    private Date updatedOn;

    public LocalDateTime getCreatedAt() {
        return createdOn.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        // it converts Date to LocalDateTime and  explains why we need this conversion
        // because we want to use LocalDateTime in our application for better date and time handling

    }
}
