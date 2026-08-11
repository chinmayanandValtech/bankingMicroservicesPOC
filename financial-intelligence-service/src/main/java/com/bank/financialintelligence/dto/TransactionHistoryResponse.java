package com.bank.financialintelligence.dto;

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

    private String transactionType;

    private BigDecimal amount;

    private String fromAccountNumber;

    private String toAccountNumber;

    private String status;

    private String remarks;

    private LocalDateTime transactionTime;
}