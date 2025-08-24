package com.res.server.walletservice.controller;

import com.res.server.walletservice.dto.TopUpRequest;
import com.res.server.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("/update")
    public ResponseEntity<String> updateWallet(@RequestParam String senderId, @RequestParam String receiverId, @RequestParam Long amount) {
        // This method will handle wallet updates
        // Implementation will be added later
        String msg=null;

        try {
         msg=walletService.updateWallet(senderId, receiverId, amount);
        } catch (RuntimeException e) {
           ResponseEntity<String> responseEntity= new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }

return  new ResponseEntity<>(msg,HttpStatus.OK);

    }

    // WalletController.java (top-up feature)
    @PostMapping("/topup")
    public ResponseEntity<String> topUpWallet(@RequestBody TopUpRequest request) {
        // Validate the request (e.g., check if userId and amount are valid)
        try {
            String transactionId = walletService.topUpWallet(request.getUserId(), request.getAmount());
            return ResponseEntity.ok("Wallet top-up successful. Transaction ID: " + transactionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



}
