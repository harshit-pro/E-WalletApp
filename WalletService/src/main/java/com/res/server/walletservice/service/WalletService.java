package com.res.server.walletservice.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.res.server.walletservice.model.Wallet;
import com.res.server.walletservice.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class WalletService {
    Long initialBalance=2000L; // Initial balance for the wallet, can be set to any value
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {"user_created"}, groupId = "jbdl123")
    public void createWallet(String message) { // message is the user data in JSON format
try {
    // here we are parsing the JSON message to a JSONObject
    JSONObject userJsonObject = (JSONObject) new JSONParser().parse(message);

    String mobileNumber= userJsonObject.get("phone").toString();

    Wallet wallet= Wallet.builder() //  yaha par we are creating a wallet object
            // using the builder pattern matlab ki we are using the builder pattern to create a wallet object
            // why because of this we can create a wallet object with the required fields
            .walletId(mobileNumber)
            .currency("INR") // Assuming the currency is Indian Rupee
            .balance(initialBalance) // Initial balance is set to 0
            .build();
    walletRepository.save(wallet);
}catch (ParseException e){
    throw  new RuntimeException("Error parsing JSON message: " + e.getMessage());
}


    }
    public String updateWallet(String senderId, String receiverId, Long amount) {
        // This method will be used to update the wallet balance after a transaction
        // For now, we will just print the details
        Wallet senderWallet = walletRepository.findWalletByWalletId(senderId);
        Wallet receiverWallet = walletRepository.findWalletByWalletId(receiverId);
        if(senderWallet==null || receiverWallet==null || senderWallet.getBalance() < amount) {
            throw new RuntimeException("Wallet not found for sender or receiver");
        }
        // Update the sender's wallet balance
        walletRepository.updateWallet(senderId, -amount);
        // Update the receiver's wallet balance
        walletRepository.updateWallet(receiverId, amount);

        return "Wallet updated successfully for transaction ID: " ;

    }

    public String topUpWallet(String userId, Long amount) {
        Wallet wallet = walletRepository.findWalletByWalletId(userId);
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for user: " + userId);
        }
        walletRepository.updateWallet(userId, amount);

        // Create a top-up transaction event and send via Kafka to transaction service
        JSONObject eventJson = new JSONObject();
        eventJson.put("senderId", "WALLET_SYSTEM");
        eventJson.put("receiverId", userId);
        eventJson.put("amount", amount);
        eventJson.put("reason", "TOP_UP");
        eventJson.put("status", "SUCCESS");
        eventJson.put("externalTransactionId", UUID.randomUUID().toString());
        eventJson.put("createdAt", LocalDateTime.now().toString());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            kafkaTemplate.send("topup_transaction_created", objectMapper.writeValueAsString(eventJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to publish Kafka message: " + e.getMessage());
        }

        return (String) eventJson.get("externalTransactionId");
    }


}
