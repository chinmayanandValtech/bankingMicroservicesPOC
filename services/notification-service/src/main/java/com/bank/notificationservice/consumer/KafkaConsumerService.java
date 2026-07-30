package com.bank.notificationservice.consumer;

import com.bank.notificationservice.event.MoneyTransferredEvent;
import com.bank.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    private final NotificationService notificationService;

    public KafkaConsumerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "money-transferred",
            groupId = "notification-group"
    )
    public void consume(MoneyTransferredEvent event) {

        notificationService.sendNotification(event);

    }

}
