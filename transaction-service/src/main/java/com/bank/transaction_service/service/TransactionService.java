package com.bank.transaction_service.service;

import com.bank.transaction_service.dto.*;

import java.util.List;

public interface TransactionService {

    TransactionResponse moneyDeposit(DepositRequest request);

    TransactionResponse moneyWithdraw(WithdrawRequest request);
    List<TransactionHistoryResponse> transactionHistory(String accountNumber);
}
