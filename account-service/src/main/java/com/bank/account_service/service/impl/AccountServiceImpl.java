package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerFeignClient;
import com.bank.account_service.dto.AccountBalanceResponse;
import com.bank.account_service.dto.AccountRequest;
import com.bank.account_service.dto.AccountResponse;
import com.bank.account_service.dto.CustomerDto;
import com.bank.account_service.dto.DepositRequest;
import com.bank.account_service.dto.TransferRequest;
import com.bank.account_service.dto.TransferResponse;
import com.bank.account_service.dto.WithdrawRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.InsufficientBalanceException;
import com.bank.account_service.exception.ResourceNotFoundException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerFeignClient customerFeignClient;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerFeignClient customerFeignClient) {
        this.accountRepository = accountRepository;
        this.customerFeignClient = customerFeignClient;
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
        CustomerDto customer = customerFeignClient.getCustomerById(accountRequest.getCustomerId());

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

        CustomerDto customer = customerFeignClient.getCustomerById(account.getCustomerId());

        return toResponse(account, customer);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> {
                    CustomerDto customer;
                    try {
                        customer = customerFeignClient.getCustomerById(account.getCustomerId());
                    } catch (ResourceNotFoundException ex) {
                        customer = null;
                    }
                    return toResponse(account, customer);
                })
                .toList();
    }

    @Override
    public AccountResponse updateAccount(Long id, AccountRequest accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));

        CustomerDto customer = customerFeignClient.getCustomerById(accountRequest.getCustomerId());

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

    @Override
    @Transactional
    public AccountBalanceResponse deposit(String accountNumber, DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        account.setBalance(account.getBalance().add(request.getAmount()));
        Account savedAccount = accountRepository.save(account);

        return AccountBalanceResponse.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .balance(savedAccount.getBalance())
                .updatedAt(savedAccount.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public AccountBalanceResponse withdraw(String accountNumber, WithdrawRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + accountNumber);
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account savedAccount = accountRepository.save(account);

        return AccountBalanceResponse.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .balance(savedAccount.getBalance())
                .updatedAt(savedAccount.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("Source and destination account cannot be the same.");
        }

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found: " + request.getFromAccountNumber()));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found: " + request.getToAccountNumber()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + request.getFromAccountNumber());
        }

        BigDecimal updatedFromBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal updatedToBalance = toAccount.getBalance().add(request.getAmount());

        fromAccount.setBalance(updatedFromBalance);
        toAccount.setBalance(updatedToBalance);

        Account savedFromAccount = accountRepository.save(fromAccount);
        Account savedToAccount = accountRepository.save(toAccount);

        return TransferResponse.builder()
                .fromAccountNumber(savedFromAccount.getAccountNumber())
                .toAccountNumber(savedToAccount.getAccountNumber())
                .amount(request.getAmount())
                .fromAccountBalance(savedFromAccount.getBalance())
                .toAccountBalance(savedToAccount.getBalance())
                .updatedAt(savedFromAccount.getUpdatedAt())
                .build();
    }
}
