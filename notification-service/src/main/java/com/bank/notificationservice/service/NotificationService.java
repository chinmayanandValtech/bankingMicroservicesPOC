package com.bank.notificationservice.service;

import com.bank.notificationservice.dto.TransactionEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(TransactionEvent event) {

        System.out.println("====================================");
        System.out.println("Notification Service");
        System.out.println("Transaction Received");
        System.out.println("Transaction Id : " + event.getTransactionId());
        System.out.println("From Account   : " + event.getFromAccount());
        System.out.println("To Account     : " + event.getToAccount());
        System.out.println("Amount         : " + event.getAmount());

        System.out.println("Sending SMS...");
        System.out.println("Sending Email...");
        System.out.println("Sending Push Notification...");

        System.out.println("Notification Sent Successfully");
        System.out.println("====================================");
    }
}