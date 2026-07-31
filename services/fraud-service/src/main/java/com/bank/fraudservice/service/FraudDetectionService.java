package com.bank.fraudservice.service;

import com.bank.fraudservice.event.MoneyTransferredEvent;
import com.bank.fraudservice.rules.FraudRule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FraudDetectionService {

    private final List<FraudRule> rules;

    public FraudDetectionService(List<FraudRule> rules) {
        this.rules = rules;
    }

    public void analyzeTransaction(MoneyTransferredEvent event) {

        for (FraudRule rule : rules) {

            if (rule.isFraud(event)) {

                System.out.println(rule.getReason());

                return;
            }
        }

        System.out.println("Transaction Approved");

    }
}
