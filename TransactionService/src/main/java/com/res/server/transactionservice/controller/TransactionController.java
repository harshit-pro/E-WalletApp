package com.res.server.transactionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.res.server.transactionservice.dto.TransactionCreateRequest;
import com.res.server.transactionservice.dto.TransactionResponseDTO;
import com.res.server.transactionservice.service.TransactionServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionServices transactionServices;


    @PostMapping("/initiate")
    private String initiateTransaction(@RequestBody @Valid TransactionCreateRequest transactionCreateRequest) throws JsonProcessingException {
        //transaction tabhi ho payega  jab user pehle se logged in ho aur uska wallet balance sufficient ho
        // security context holder se user ki details le lo
        // yaha par transactions object me senderId ko set kar do jo ki user ki mobile number se aayega
        // aur receiverId ko bhi set kar do jo ki receiver ki mobile number se aayega
        // i get the user details from current logged in user from security context holder
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Sender Username: " + sender.getUsername());
        System.out.println("Receiver ID: " + transactionCreateRequest.getReceiverId());
        return transactionServices.transact(transactionCreateRequest, sender.getUsername());
    }

        @GetMapping("/history")
        public Page<TransactionResponseDTO> getHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String senderId,
        @RequestParam(required = false) String receiverId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
            return transactionServices.getTransactionHistory(page, size, senderId, receiverId, status, fromDate, toDate);// Call service with filters
        }
}
