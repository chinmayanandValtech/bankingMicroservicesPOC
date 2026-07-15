package com.bank.account_service.service;

import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);

    AccountResponse getAccountById(String accountNumber);

    List<AccountResponse> getAllAccounts();

    AccountResponse updateAccount(Long id, AccountRequest accountRequest);

    void deleteAccount(Long id);
}
