package com.bank.fraudservice.consumer;

import com.bank.fraudservice.event.MoneyTransferredEvent;
import com.bank.fraudservice.service.FraudDetectionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    private final FraudDetectionService fraudDetectionService;

    public KafkaConsumerService(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @KafkaListener(
            topics = "money-transferred",
            groupId = "fraud-group"
    )
    public void consume(MoneyTransferredEvent event) {

        fraudDetectionService.analyzeTransaction(event);

    }
}
