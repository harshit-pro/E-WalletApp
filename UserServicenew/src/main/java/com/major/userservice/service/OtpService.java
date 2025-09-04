//package com.major.userservice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OtpService {
//
//    private final StringRedisTemplate redisTemplate;
//    private final String OTP_KEY_PREFIX = "otp:";
//
//    public String generateOtp(String username) {
//        String otp = String.format("%06d", new Random().nextInt(999999));
//        redisTemplate.opsForValue().set(
//                OTP_KEY_PREFIX + username,
//                otp,
//                5, TimeUnit.MINUTES
//        );
//        log.info("Generated OTP [{}] for username [{}]", otp, username);
//        return otp;
//    }
//
//    public boolean validateOtp(String username, String otp) {
//        String storedOtp = redisTemplate.opsForValue().get(OTP_KEY_PREFIX + username);
//        if (storedOtp == null) {
//            log.warn("OTP expired or not found for username [{}]", username);
//            return false;
//        }
//        boolean valid = storedOtp.equals(otp);
//        log.info("OTP validation for [{}]: {}", username, valid ? "SUCCESS" : "FAILURE");
//        return valid;
//    }
//
//    public void deleteOtp(String username) {
//        redisTemplate.delete(OTP_KEY_PREFIX + username);
//        log.info("Deleted OTP for username [{}]", username);
//    }
//}
