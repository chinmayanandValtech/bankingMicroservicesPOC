package com.bank.transaction_service.dto;

import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {

    private String transactionReference;

    private TransactionType transactionType;

    private BigDecimal amount;

    private String fromAccountNumber;

    private String toAccountNumber;

    private TransactionStatus status;

    private String remarks;

    private LocalDateTime transactionTime;
}
