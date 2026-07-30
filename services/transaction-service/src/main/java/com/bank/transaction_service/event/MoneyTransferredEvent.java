package com.bank.transaction_service.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MoneyTransferredEvent {

    private String transactionReference;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private LocalDateTime transactionTime;

    public MoneyTransferredEvent() {
    }

    public MoneyTransferredEvent(
            String transactionReference,
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount,
            LocalDateTime transactionTime) {

        this.transactionReference = transactionReference;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }

    // getters and setters
}