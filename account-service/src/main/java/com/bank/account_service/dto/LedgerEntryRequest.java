package com.bank.account_service.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Asks transaction-service to write a history record for money this service has
 * already moved. Used for the opening deposit, which happens at account creation
 * and so cannot go through the normal transaction-service flow.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntryRequest {

    private String transactionType;

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private String remarks;
}
