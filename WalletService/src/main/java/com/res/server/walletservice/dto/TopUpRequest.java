// TopUpRequest.java
package com.res.server.walletservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUpRequest {
    private String userId;
    private Long amount;
}