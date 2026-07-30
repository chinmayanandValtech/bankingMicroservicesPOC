package com.bank.transaction_service.consumer;

import com.bank.transaction_service.event.MoneyTransferredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "money-transferred", groupId = "account-group")
    public void consume(MoneyTransferredEvent event) {

        System.out.println("Received Event: " + event.getTransactionReference() + ", From: " + event.getFromAccountNumber() + ", To: " + event.getToAccountNumber() + ", Amount: " + event.getAmount() + ", Time: " + event.getTransactionTime());

    }
}
