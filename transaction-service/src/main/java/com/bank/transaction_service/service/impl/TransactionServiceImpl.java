package com.bank.transaction_service.service.impl;

import com.bank.transaction_service.client.AccountClient;
import com.bank.transaction_service.dto.*;
import com.bank.transaction_service.entity.Transaction;
import com.bank.transaction_service.enums.TransactionStatus;
import com.bank.transaction_service.enums.TransactionType;
import com.bank.transaction_service.exception.ResourceNotFoundException;
import com.bank.transaction_service.repository.TransactionRepository;
import com.bank.transaction_service.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountClient accountClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountClient accountClient) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
    }


    @Override
    public TransactionResponse deposit(DepositRequest request) {

        AccountBalanceResponse account =
                accountClient.deposit(
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

    @Override
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

        transactionRepository.save(transaction);

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
}
