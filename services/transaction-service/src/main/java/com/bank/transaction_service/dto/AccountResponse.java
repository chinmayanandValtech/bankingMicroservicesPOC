package com.bank.transaction_service.dto;


import com.bank.transaction_service.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private Long customerId;

    private String customerName;

    private BigDecimal balance;

    private AccountStatus status;

    private LocalDateTime createdAt;
}
