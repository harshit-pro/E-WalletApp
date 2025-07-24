package com.res.server.transactionservice.dto;

import com.res.server.transactionservice.model.TransactionStatus;
import com.res.server.transactionservice.service.TransactionServices;
import lombok.*;
import org.hibernate.query.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionResponseDTO {

   private String transactionId;
    private String senderId;
    private String receiverId;
    private Long amount;
    private String reason;
    private String status;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;

}
