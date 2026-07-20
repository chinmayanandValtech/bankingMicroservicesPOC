package com.bank.account_service.service;

import com.bank.account_service.dto.AccountBalanceResponse;
import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.DepositRequest;
import com.bank.account_service.dto.TransferRequest;
import com.bank.account_service.dto.TransferResponse;
import com.bank.account_service.dto.WithdrawRequest;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse getAccountById(String accountNumber);

    List<AccountResponse> getAllAccounts();

    AccountResponse updateAccount(Long id, AccountRequest accountRequest);

    void deleteAccount(Long id);

    AccountBalanceResponse deposit(String accountNumber, DepositRequest request);

    AccountBalanceResponse withdraw(String accountNumber, WithdrawRequest request);

    TransferResponse transfer(TransferRequest request);
}
