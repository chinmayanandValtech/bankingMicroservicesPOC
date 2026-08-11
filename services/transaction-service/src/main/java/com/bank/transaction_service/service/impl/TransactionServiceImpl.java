package com.bank.transaction_service.service.impl;

import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.client.AccountFeignClient;
import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import com.bank.transaction_service.event.MoneyTransferredEvent;
import com.bank.transaction_service.exception.ResourceNotFoundException;
// Kafka disabled for now, see constructor and transfer() below to re-enable
// import com.bank.transaction_service.producer.KafkaProducerService;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountFeignClient accountFeignClient;
    // private final KafkaProducerService kafkaProducerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountClient accountClient, AccountFeignClient accountFeignClient) {
        this.transactionRepository = transactionRepository;
        this.accountFeignClient = accountFeignClient;
        // this.kafkaProducerService = kafkaProducerService;
    }


    @Override
    @Retry(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    public TransactionResponse deposit(DepositRequest request) {

        AccountBalanceResponse account =
                accountFeignClient.deposit(
                        request.getAccountNumber(),
                        request);

        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(TransactionType.DEPOSIT)
                .toAccountNumber(request.getAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionReference(transaction.getTransactionReference())
                .amount(transaction.getAmount())
                .updatedBalance(account.getBalance())
                .status(transaction.getStatus())
                .transactionTime(transaction.getCreatedAt())
                .build();
    }

    public TransactionResponse withdrawFallback(
            WithdrawRequest request,
            Exception ex) {

        return TransactionResponse.builder()
                .status(TransactionStatus.FAILED)
                .amount(request.getAmount())
                .build();
    }
    @Override
    @Retry(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    public TransactionResponse withdraw(WithdrawRequest request) {
        System.out.println("Calling Account Service...");
        AccountBalanceResponse account =
                accountFeignClient.withdraw(
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

        transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionReference(transaction.getTransactionReference())
                .amount(transaction.getAmount())
                .updatedBalance(account.getBalance())
                .status(transaction.getStatus())
                .transactionTime(transaction.getCreatedAt())
                .build();
    }



    @Override
    @Retry(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "withdrawFallback"
    )
    public TransactionResponse transfer(TransferRequest request) {
        TransferResponse account =
                accountFeignClient.transfer(request);

        Transaction transaction = Transaction.builder()
                .transactionReference(UUID.randomUUID().toString())
                .transactionType(TransactionType.TRANSFER)
                .toAccountNumber(request.getToAccountNumber())
                .fromAccountNumber(request.getFromAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .build();

        transactionRepository.save(transaction);


        // Kafka event publishing disabled for now; uncomment to re-enable once Kafka is wired up again
        // try {
        //     MoneyTransferredEvent event = new MoneyTransferredEvent(
        //             transaction.getTransactionReference(),
        //             transaction.getFromAccountNumber(),
        //             transaction.getToAccountNumber(),
        //             transaction.getAmount(),
        //             transaction.getUpdatedAt()
        //     );
        //
        //     kafkaProducerService.publish(event);
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }

        return TransactionResponse.builder()
                .transactionReference(transaction.getTransactionReference())
                .amount(transaction.getAmount())
                .updatedBalance(account.getFromAccountBalance())
                .status(transaction.getStatus())
                .transactionTime(transaction.getCreatedAt())
                .build();
    }

    @Override
    public List<TransactionHistoryResponse> getTransactionHistory(String accountNumber) {
        accountFeignClient.getAccountByAccountNumber(accountNumber);

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
}
