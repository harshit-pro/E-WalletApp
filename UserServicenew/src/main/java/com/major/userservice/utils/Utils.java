package com.major.userservice.utils;

import com.major.userservice.dto.CreateUserRequest;
import com.major.userservice.dto.GetUserResponse;
import com.major.userservice.models.User;

public class Utils {
    // Utility class to convert between DTOs and Entity models
    // its because we are using Lombok to generate getters, setters, and builders
    //so we don't need to write boilerplate code for these conversions
    // this class is used to convert CreateUserRequest to User entity and User entity to GetUserResponse
    public static User convertUserCreateRequest(CreateUserRequest createUserRequest) {
        System.out.println("Converting CreateUserRequest to User entity: " + createUserRequest);
        return User.builder()
                .username(createUserRequest.getMobileNumber())
                .password(createUserRequest.getPassword())
                .name(createUserRequest.getName())
                .email(createUserRequest.getEmail())
                .age(createUserRequest.getAge()).build();
    }
    public static GetUserResponse convertToGetUserResponse(User user) {
        return GetUserResponse.builder()
                .name(user.getName())
                .age(user.getAge())
                .email(user.getEmail())
                .mobile(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
