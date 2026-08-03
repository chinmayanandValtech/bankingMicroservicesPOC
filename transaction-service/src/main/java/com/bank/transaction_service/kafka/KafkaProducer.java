package com.bank.transaction_service.kafka;


import com.bank.transaction_service.event.MoneyTransferredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, MoneyTransferredEvent> kafkaTemplate;

    private static final String TOPIC = "money-transferred";

    public void publish(MoneyTransferredEvent event) {

        kafkaTemplate.send(TOPIC, event);

        System.out.println("Event Published : " + event);

    }
}