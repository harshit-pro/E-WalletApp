package com.major.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.major.userservice.dao.UserRepository;
import com.major.userservice.dto.CreateUserRequest;
import com.major.userservice.dto.GetUserResponse;
import com.major.userservice.models.User;
import com.major.userservice.service.UserService;
import com.major.userservice.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws JsonProcessingException {
        try {
            System.out.println("Received user creation request for username: " + createUserRequest.getMobileNumber());
            userService.create(Utils.convertUserCreateRequest(createUserRequest));
            System.out.println("User creation initiated for username: " + createUserRequest.getMobileNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body("User creation initiated. Please verify OTP.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error during user creation for username: " + createUserRequest.getMobileNumber());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    /*Profile Information*/
    @GetMapping("/profile-info")
    public GetUserResponse getProfile() {
        // getting the object of logged-in user from the security context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // this statement retrieves the authenticated user from the security context
        // SecurityContextHolder is a class that holds the security context of the current thread
        // getAuthentication() returns the Authentication object which contains the user details
        // getPrincipal() returns the principal object which is the authenticated user
        // so we can get the user details from the principal object
        user = userService.getById(user.getId()); // we are getting the user details from the database using the user id
        return Utils.convertToGetUserResponse(user); // this method converts the User entity to GetUserResponse DTO
    }
    //to be used by the other services for authentication using open feign client
    @GetMapping(value = "/username/{username}" , produces = "application/json")
    public User getUserByUsername(@PathVariable("username")  String username) {
        return   (User) userService.loadUserByUsername(username);
    }
//    @GetMapping("/history")
//    public Page<TransactionResponseDTO> getHistory(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String senderId,
//            @RequestParam(required = false) String receiverId,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
//    ) {
//        return transactionService.getTransactionHistory(...); // Call service with filters
//    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String otp) throws JsonProcessingException {
        try {
            System.out.println("Verifying OTP for username: " + username);
            if (userService.verifyOtpAndCreateUser(username, otp)) {
                return ResponseEntity.ok("✅ Email verified and user registered successfully");
            } else {
                System.out.println("Invalid or expired OTP for username: " + username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid or expired OTP");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error during OTP verification for username: " + username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during OTP verification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ An error occurred during verification");
        }
    }
}