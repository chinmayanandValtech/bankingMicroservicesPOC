package com.bank.fraudservice.rules;

import com.bank.fraudservice.event.MoneyTransferredEvent;

public interface FraudRule {

    boolean isFraud(MoneyTransferredEvent event);

    String getReason();

}
