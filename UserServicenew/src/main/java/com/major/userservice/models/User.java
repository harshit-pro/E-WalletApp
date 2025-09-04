package com.major.userservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails, Serializable { // why it implements UserDetails and Serializable
    // because it is used for authentication and authorization in Spring Security
    // UserDetails is an interface that provides core user information
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // This is the primary key

    private String name; // This is the name of the user
    private String email;
    // why we add this field
    // This is used as the login identifier (can be phone or username)
    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @JsonIgnore
    private String authorities;
    private int age;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private boolean IsVerified; // This flag is used to block login until email/OTP verification

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // about this method
        // This method is used to get the authorities of the user
        // The authorities are stored as a string in the format "role1::role2::role3"
        return Arrays.stream(this.authorities.split("::"))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
