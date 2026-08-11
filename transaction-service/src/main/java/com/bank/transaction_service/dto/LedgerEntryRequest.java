package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * A record-only request from another service for money it has already moved
 * itself (currently the opening deposit at account creation). Unlike the normal
 * deposit/withdraw/transfer flows, this writes history without touching balances.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntryRequest {

    @NotNull(message = "Transaction type is required")
    private String transactionType;

    private String fromAccountNumber;

    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Size(max = 255)
    private String remarks;
}
