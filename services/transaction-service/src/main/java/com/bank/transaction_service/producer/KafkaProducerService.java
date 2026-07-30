package com.bank.transaction_service.producer;


import com.bank.transaction_service.event.MoneyTransferredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "money-transferred";

    private final KafkaTemplate<String, MoneyTransferredEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, MoneyTransferredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(MoneyTransferredEvent event) {
        kafkaTemplate.send(TOPIC, event);

        System.out.println("Published event: " + event.getTransactionReference() + ", From: " + event.getFromAccountNumber() + ", To: " + event.getToAccountNumber() + ", Amount: " + event.getAmount() + ", Time: " + event.getTransactionTime());
    }
}