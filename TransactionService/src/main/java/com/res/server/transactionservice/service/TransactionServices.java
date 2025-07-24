package com.res.server.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.res.server.transactionservice.dao.TransactionDao;
import com.res.server.transactionservice.dao.WalletInterface;
import com.res.server.transactionservice.dto.TransactionCreateRequest;
import com.res.server.transactionservice.dto.TransactionResponseDTO;
import com.res.server.transactionservice.model.TransactionStatus;
import com.res.server.transactionservice.model.Transactions;
import feign.FeignException;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServices {
    @Autowired
    TransactionDao transactionDao;
    @Autowired
    WalletInterface walletInterface;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String transact(@Valid TransactionCreateRequest transactionCreateRequest, String senderId) throws JsonProcessingException {


        Transactions transaction = Transactions.builder()
                .senderId(senderId)
                .receiverId(transactionCreateRequest.getReceiverId())
                .amount(transactionCreateRequest.getAmount())
                .reason(transactionCreateRequest.getReason())
                .status(TransactionStatus.PENDING)
                .externalTransactionId(UUID.randomUUID().toString())

                .build();
        transactionDao.save(transaction);
        TransactionStatus transactionStatus = null;
        // After saving the transaction, update the wallet balance
        try {
            ResponseEntity<String> responseEntity = this.walletInterface.updateWallet(
                    transaction.getSenderId(),
                    transaction.getReceiverId(),
                    transactionCreateRequest.getAmount()
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                transactionStatus = TransactionStatus.SUCCESS;
            } else {
                // If the wallet service returns an error, we mark the transaction as failed
                // errors are typically 4xx or 5xx responses
                transactionStatus = TransactionStatus.FAILED;
            }
        } catch (FeignException e) {
            transactionStatus = TransactionStatus.FAILED;
            // Log the error
        }
        // publish an event to the message broker (like Kafka) to notify other services
        // that a transaction has been initiated

        // update the wallet balances in the wallet service
        // Using a REST call to the wallet service to update the balances and endpoint

        // Update the transaction status in the database
        transactionDao.updateTransactionStatus(transactionStatus, transaction.getExternalTransactionId());

        // TODO - Find email addresses using sender and reciever id / mobile number
        String senderEmail = "r954025@gmail.com";
        String receiverEmail = "abhimishranav@gmail.com";
        // here we sending the notification to the user about the transaction status
        // Todo:Send a notification using notification-service
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("transactionId", transaction.getExternalTransactionId());
        jsonObject.put("transactionStatus", transactionStatus);
        jsonObject.put("amount", transaction.getAmount());
        jsonObject.put("senderEmail", senderEmail);
        jsonObject.put("receiverEmail", receiverEmail);

        this.kafkaTemplate.send("transaction_completed", objectMapper.writeValueAsString(jsonObject));


        return transaction.getExternalTransactionId();
    }


    public Page<TransactionResponseDTO> getTransactionHistory(int page, int size, String senderId, String receiverId, String status, LocalDate fromDate, LocalDate toDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<Transactions> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (senderId != null) predicates.add(cb.equal(root.get("senderId"), senderId));
            if (receiverId != null) predicates.add(cb.equal(root.get("receiverId"), receiverId));
            if (status != null) predicates.add(cb.equal(root.get("status"), TransactionStatus.valueOf(status)));
            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(23, 59, 59)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return transactionDao.findAll(spec, pageable)
                .map(txn -> TransactionResponseDTO.builder()
                        .transactionId(txn.getExternalTransactionId())
                        .senderId(txn.getSenderId())
                        .receiverId(txn.getReceiverId())
                        .amount(txn.getAmount())
                        .reason(txn.getReason())
                        .status(String.valueOf(txn.getStatus()))
                        .createdAt(txn.getCreatedAt())
                        .build());
    }
    @KafkaListener(topics = {"topup_transaction_created"}, groupId = "jbdl123")
    public void createTopUpTransaction(String message) {
        try {
            JSONObject json = null;
            LocalDateTime createdOnLocalDateTime = LocalDateTime.parse((String) json.get("createdOn"));
            Date createdOnDate = Date.from(createdOnLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            json = (JSONObject) new JSONParser().parse(message);
            Transactions txn = Transactions.builder()
                    .senderId((String) json.get("senderId"))
                    .receiverId((String) json.get("receiverId"))
                    .amount((Long) json.get("amount"))
                    .reason((String) json.get("reason"))
                    .status(TransactionStatus.valueOf((String) json.get("status")))
                    .externalTransactionId((String) json.get("externalTransactionId"))
                    .createdOn(createdOnDate)
                            .build();
            transactionDao.save(txn);
        } catch (Exception e) {
            // Log the error
            System.out.println("Top-up transaction parse error: " + e.getMessage());
        }
    }

}
