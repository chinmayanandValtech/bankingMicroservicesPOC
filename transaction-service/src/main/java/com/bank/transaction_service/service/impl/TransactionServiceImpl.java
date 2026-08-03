package com.bank.transaction_service.service.impl;

import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import com.bank.transaction_service.event.MoneyTransferredEvent;
import com.bank.transaction_service.kafka.KafkaProducer;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final KafkaProducer kafkaProducer;

    @Override
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "depositFallback")
    public TransactionResponse deposit(DepositRequest request) {

        AccountBalanceResponse account;

        try {
            account = accountClient.deposit(
                    request.getAccountNumber(),
                    request);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(TransactionType.DEPOSIT)
                .toAccountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionReference(savedTransaction.getTransactionReference())
                .amount(savedTransaction.getAmount())
                .updatedBalance(account.getBalance())
                .status(savedTransaction.getStatus())
                .transactionTime(savedTransaction.getCreatedAt())
                .build();
    }

    public TransactionResponse depositFallback(
            DepositRequest request,
            Exception ex) {

        System.out.println("============== DEPOSIT FAILED ==============");
        ex.printStackTrace();

        throw new RuntimeException(ex);
    }

    @Override
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback")
    public TransactionResponse withdraw(WithdrawRequest request) {

        AccountBalanceResponse account =
                accountClient.withdraw(
                        request.getAccountNumber(),
                        request);

        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(TransactionType.WITHDRAWAL)
                .toAccountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionReference(savedTransaction.getTransactionReference())
                .amount(savedTransaction.getAmount())
                .updatedBalance(account.getBalance())
                .status(savedTransaction.getStatus())
                .transactionTime(savedTransaction.getCreatedAt())
                .build();
    }

    public TransactionResponse withdrawFallback(
            WithdrawRequest request,
            Exception ex) {

        return TransactionResponse.builder()
                .transactionReference("FAILED")
                .amount(request.getAmount())
                .status(TransactionStatus.FAILED)
                .updatedBalance(BigDecimal.ZERO)
                .build();
    }

    @Override
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "transferFallback")
    public TransactionResponse transfer(TransferRequest request) {

        TransferResponse account =
                accountClient.transfer(request);

        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(TransactionType.TRANSFER)
                .toAccountNumber(request.getToAccountNumber())
                .fromAccountNumber(request.getFromAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Create Kafka Event
        MoneyTransferredEvent event = MoneyTransferredEvent.builder()
                .transactionId(savedTransaction.getId())
                .fromAccount(savedTransaction.getFromAccountNumber())
                .toAccount(savedTransaction.getToAccountNumber())
                .amount(savedTransaction.getAmount())
                .build();

        // Publish Event to Kafka
        kafkaProducer.publish(event);

        return TransactionResponse.builder()
                .transactionReference(savedTransaction.getTransactionReference())
                .amount(savedTransaction.getAmount())
                .updatedBalance(account.getFromAccountBalance())
                .status(savedTransaction.getStatus())
                .transactionTime(savedTransaction.getCreatedAt())
                .build();
    }

    public TransactionResponse transferFallback(
            TransferRequest request,
            Exception ex) {

        return TransactionResponse.builder()
                .transactionReference("FAILED")
                .amount(request.getAmount())
                .status(TransactionStatus.FAILED)
                .updatedBalance(BigDecimal.ZERO)
                .build();
    }

    @Override
    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "historyFallback")
    public List<TransactionHistoryResponse> getTransactionHistory(
            String accountNumber) {

        accountClient.getAccountByAccountNumber(accountNumber);

        List<Transaction> transactions =
                transactionRepository.findAllByAccountNumber(accountNumber);

        return transactions.stream()
                .map(transaction -> TransactionHistoryResponse.builder()
                        .transactionReference(transaction.getTransactionReference())
                        .transactionType(transaction.getTransactionType())
                        .fromAccountNumber(transaction.getFromAccountNumber())
                        .toAccountNumber(transaction.getToAccountNumber())
                        .amount(transaction.getAmount())
                        .status(transaction.getStatus())
                        .remarks(transaction.getRemarks())
                        .transactionTime(transaction.getCreatedAt())
                        .build())
                .toList();
    }

    public List<TransactionHistoryResponse> historyFallback(
            String accountNumber,
            Exception ex) {

        return List.of();
    }
}