package com.res.server.notificationservices.service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics = {"transaction_completed"}, groupId = "jbdl123")
    public void notify(String msg) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(msg);
        String transactionId = (String) jsonObject.get("transactionId");
        String transactionStatus = (String) jsonObject.get("transactionStatus");
        Long amount = (Long) jsonObject.get("amount");
        String senderEmail = (String) jsonObject.get("senderEmail");
        String receiverEmail = (String) jsonObject.get("receiverEmail");

        String senderMsg = getSenderMessage(transactionStatus, amount, transactionId);
        String receiverMsg = getReceiverMessage(transactionStatus, amount, senderEmail);

        if (!senderMsg.isEmpty()) {
            simpleMailMessage.setTo(senderEmail);
            simpleMailMessage.setSubject("E-wallet Transaction Update");
            simpleMailMessage.setFrom("harshitesting07@gmail.com");
            simpleMailMessage.setText(senderMsg);
            javaMailSender.send(simpleMailMessage);
        }

        if (!receiverMsg.isEmpty()) {
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("E-wallet Transaction Update");
            simpleMailMessage.setFrom("harshitesting07@gmail.com");
            simpleMailMessage.setText(receiverMsg);
            javaMailSender.send(simpleMailMessage);
        }
    }

    @KafkaListener(topics = {"otp-verification"}, groupId = "jbdl123")
    public void sendOtpVerification(String msg) throws ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(msg);
        String email = (String) json.get("email");
        String otp = (String) json.get("otp");

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("OTP for Wallet Verification");
        simpleMailMessage.setFrom("harshitesting07@gmail.com");
        simpleMailMessage.setText("Your OTP is: " + otp + ". It is valid for 5 minutes.");

        javaMailSender.send(simpleMailMessage);
    }

    private String getSenderMessage(String transactionStatus, Long amount, String transactionId) {
        if (transactionStatus.equals("FAILURE")) {
            return "Hi! Your transaction of amount ₹" + amount + ", ID = " + transactionId + " has Failed.";
        } else {
            return "Hi! Your account has been debited with ₹" + amount + ", transaction ID = " + transactionId + ".";
        }
    }

    private String getReceiverMessage(String transactionStatus, Long amount, String senderEmail) {
        if (transactionStatus.equals("SUCCESSFUL")) {
            return "Hi! Your account has been credited with ₹" + amount + " from user " + senderEmail + ".";
        }
        return "";
    }
}
