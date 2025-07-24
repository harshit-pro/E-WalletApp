package com.res.server.walletservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(nullable = false,unique = true) // unique = true means that the walletId should be unique
    // it is basically a user's mobile number.
    private String walletId; // it is basically a user's  mobile number.

    private Long balance; // 19837-> 198.37 INR
    private String currency; // INR, USD, EUR, etc.

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

}
