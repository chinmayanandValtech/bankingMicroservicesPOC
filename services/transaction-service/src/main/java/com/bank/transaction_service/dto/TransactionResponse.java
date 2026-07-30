package com.bank.transaction_service.dto;

import com.bank.transaction_service.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String transactionReference;
    private BigDecimal amount;
    private BigDecimal updatedBalance;

    private TransactionStatus status;
    private LocalDateTime transactionTime;
}
