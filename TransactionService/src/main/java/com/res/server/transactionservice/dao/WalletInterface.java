package com.res.server.transactionservice.dao;


import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("WALLET-SERVICE")
// must match the service name registered in Eureka
public interface WalletInterface {

    // Define methods to interact with the Wallet Service
    // For example, you might have methods like:
    // @GetMapping("/wallet/{id}")
    // Wallet getWalletById(@PathVariable("id") String id);
    // @PostMapping("/wallet/update")
    // void updateWallet(@RequestBody Wallet wallet);
    // Add more methods as needed based on the Wallet Service API
    @PostMapping("/wallet/update")
    public ResponseEntity<String> updateWallet(@RequestParam String senderId,
                                               @RequestParam String receiverId,
                                               @RequestParam Long amount);




}
