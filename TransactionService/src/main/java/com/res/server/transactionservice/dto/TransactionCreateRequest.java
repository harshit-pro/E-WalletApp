package com.res.server.transactionservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * DTO for creating a transaction request.
 * This class is used to encapsulate the data required to create a new transaction.
 */
public class TransactionCreateRequest {

    @NotBlank
    private String receiverId;
//    @NotBlank
//    private String senderId; we dont need this field as it will be derived from the authenticated user context
    // as the sender is the user who is making the request his mobile number will be used as senderId
    @Min(1)
    private Long amount;
    private String reason;


}
