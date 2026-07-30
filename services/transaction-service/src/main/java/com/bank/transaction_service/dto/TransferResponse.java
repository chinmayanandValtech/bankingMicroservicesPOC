package com.bank.transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private BigDecimal fromAccountBalance;

    private BigDecimal toAccountBalance;

    private LocalDateTime updatedAt;
}
