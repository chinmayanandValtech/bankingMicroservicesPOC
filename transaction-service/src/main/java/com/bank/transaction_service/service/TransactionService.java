package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.*;

import java.util.List;

public interface TransactionService {

    TransactionResponse deposit(DepositRequest request);

    TransactionResponse withdraw(WithdrawRequest request);

    TransactionResponse transfer(TransferRequest request);

    List<TransactionHistoryResponse> getTransactionHistory(String accountNumber);
}
