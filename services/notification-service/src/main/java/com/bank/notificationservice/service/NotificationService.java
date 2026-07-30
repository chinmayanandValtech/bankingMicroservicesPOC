package com.bank.notificationservice.service;

import com.bank.notificationservice.event.MoneyTransferredEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(MoneyTransferredEvent event) {

        System.out.println("""
                ==============================
                PAYMENT NOTIFICATION
                ==============================
                Transaction : %s
                From        : %s
                To          : %s
                Amount      : %s
                Time        : %s
                ==============================
                """
                .formatted(
                        event.getTransactionReference(),
                        event.getFromAccountNumber(),
                        event.getToAccountNumber(),
                        event.getAmount(),
                        event.getTransactionTime()));

    }
}
