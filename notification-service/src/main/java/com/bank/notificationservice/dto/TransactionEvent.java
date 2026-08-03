package com.bank.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
}