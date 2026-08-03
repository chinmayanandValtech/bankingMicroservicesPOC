package com.bank.transaction_service.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferredEvent {

    private Long transactionId;

    private String fromAccount;

    private String toAccount;

    private BigDecimal amount;
}