package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.CustomerDto;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.ResourceNotFoundException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerClient customerClient) {
        this.accountRepository = accountRepository;
        this.customerClient = customerClient;
    }

    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis());
    }

    private AccountResponse toResponse(Account account, CustomerDto customer) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .customerName(customer == null ? null : customer.getFirstName() + " " + customer.getLastName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        CustomerDto customer = customerClient.getCustomerById(accountRequest.getCustomerId());

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(accountRequest.getAccountType())
                .balance(accountRequest.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .customerId(customer.getCustomerId())
                .build();

        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount, customer);
    }

    @Override
    public AccountResponse getAccountById(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        CustomerDto customer = customerClient.getCustomerById(account.getCustomerId());

        return toResponse(account, customer);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> toResponse(account, customerClient.getCustomerById(account.getCustomerId())))
                .toList();
    }

    @Override
    public AccountResponse updateAccount(Long id, AccountRequest accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));

        CustomerDto customer = customerClient.getCustomerById(accountRequest.getCustomerId());

        account.setAccountType(accountRequest.getAccountType());
        account.setCustomerId(customer.getCustomerId());

        Account updatedAccount = accountRepository.save(account);

        return toResponse(updatedAccount, customer);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));

        accountRepository.delete(account);
    }
}
