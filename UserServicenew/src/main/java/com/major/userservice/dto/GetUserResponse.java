package com.major.userservice.dto;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserResponse {

    private String name;;
    private String email;
    private String mobile;
    private Integer age;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
