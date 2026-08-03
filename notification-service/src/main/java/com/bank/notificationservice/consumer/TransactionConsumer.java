package com.bank.notificationservice.consumer;

import com.bank.notificationservice.dto.TransactionEvent;
import com.bank.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final NotificationService notificationService;

    public TransactionConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "money-transferred",
            groupId = "notification-group"
    )
    public void consume(TransactionEvent event) {

        notificationService.sendNotification(event);
    }
}