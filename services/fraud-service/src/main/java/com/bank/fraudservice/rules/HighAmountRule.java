package com.bank.fraudservice.rules;

import com.bank.fraudservice.event.MoneyTransferredEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighAmountRule implements FraudRule {

    private static final BigDecimal LIMIT =
            new BigDecimal("50000");

    @Override
    public boolean isFraud(MoneyTransferredEvent event) {

        return event.getAmount().compareTo(LIMIT) > 0;

    }

    @Override
    public String getReason() {

        return "Amount exceeds ₹50,000";

    }
}
