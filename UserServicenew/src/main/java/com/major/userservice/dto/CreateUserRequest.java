package com.major.userservice.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String mobileNumber;
    @NotBlank
    private String password;

    private String email;
    @Min(18) // Age should be greater than 18
    private Integer age;
}
