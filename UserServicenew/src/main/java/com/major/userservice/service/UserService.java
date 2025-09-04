package com.major.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.userservice.dao.UserRepository;
import com.major.userservice.models.User;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String REG_KEY_PREFIX = "reg:"; // Registration data key
    private final String OTP_KEY_PREFIX = "otp:"; // OTP key

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        if (!user.isIsVerified()) {
            System.out.println("User not verified: " + username);
            throw new UsernameNotFoundException("Email not verified for user: " + username);
        }
        return user;
    }

    public void create(User user) throws JsonProcessingException {
        System.out.println("Creating user: " + user.getUsername());
        // Check if username already exists in the database
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        // Check if username is pending in Redis
        if (redisTemplate.opsForValue().get(REG_KEY_PREFIX + user.getUsername()) != null) {
            throw new IllegalArgumentException("Username is pending verification: " + user.getUsername());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String otp = String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP

        // Prepare user object
        user.setName(user.getName());
        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsVerified(false);


        // Save user temporarily in Redis
        redisTemplate.opsForValue().set(
                REG_KEY_PREFIX + user.getUsername(),
                objectMapper.writeValueAsString(user),
                10, TimeUnit.MINUTES
        );
        // Save OTP in Redis
        redisTemplate.opsForValue().set(
                OTP_KEY_PREFIX + user.getUsername(),
                otp,
                10, TimeUnit.MINUTES
        );
        System.out.println("OTP generated for " + user.getUsername() + ": " + otp);

        // Send OTP via Kafka
        JSONObject otpPayload = new JSONObject();
        otpPayload.put("phone", user.getUsername());
        otpPayload.put("email", user.getEmail());
        otpPayload.put("otp", otp);
        System.out.println("Sending OTP to Kafka: " + otpPayload.toString());
        kafkaTemplate.send("send-otp", objectMapper.writeValueAsString(otpPayload));
    }

    @Transactional
    public boolean verifyOtpAndCreateUser(String username, String otp) throws JsonProcessingException {
        System.out.println("Verifying OTP for user: " + username);
        String storedOtp = redisTemplate.opsForValue().get(OTP_KEY_PREFIX + username);
        if (storedOtp == null) {
            System.out.println("OTP not found or expired for user: " + username);
            throw new IllegalArgumentException("OTP expired or invalid.");
        }
        if (!storedOtp.equals(otp)) {
            System.out.println("Invalid OTP for user: " + username);
            return false;
        }

        String userJson = redisTemplate.opsForValue().get(REG_KEY_PREFIX + username);
        if (userJson == null) {
            System.out.println("User registration data not found for username: " + username);
            throw new IllegalArgumentException("Registration expired. Please register again.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        User user = objectMapper.readValue(userJson, User.class);

        // Double-check username doesn't exist in DB
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redisTemplate.delete(REG_KEY_PREFIX + username);
            redisTemplate.delete(OTP_KEY_PREFIX + username);
            throw new IllegalArgumentException("Username already exists in database: " + user.getUsername());
        }
        user.setIsVerified(true);
        user.setAuthorities("usr"); // Set default authority
        userRepository.save(user);
        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("phone", user.getUsername());
        userJsonObject.put("email", user.getEmail());
        try {
            //  publish an event to kafka with topic -> user_created
            kafkaTemplate.send("user_created", objectMapper.writeValueAsString(userJsonObject));
            System.out.println("User created event sent to Kafka for user: " + username);
        }catch (JsonProcessingException e) {
            System.out.println("Error sending user created event to Kafka: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        // Clean up Redis
        redisTemplate.delete(REG_KEY_PREFIX + username);
        redisTemplate.delete(OTP_KEY_PREFIX + username);
        System.out.println("User registration completed successfully for: " + username);
        return true;
    }
    public User getById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}