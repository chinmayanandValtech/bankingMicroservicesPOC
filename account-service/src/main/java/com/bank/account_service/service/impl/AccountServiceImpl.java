package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.client.TransactionLedgerClient;
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
import com.bank.account_service.exception.ForbiddenException;
import com.bank.account_service.exception.InsufficientBalanceException;
import com.bank.account_service.exception.ResourceNotFoundException;
import com.bank.account_service.exception.ServiceUnavailableException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.CurrentUser;
import com.bank.account_service.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final CurrentUser currentUser;
    private final TransactionLedgerClient transactionLedgerClient;

    public AccountServiceImpl(AccountRepository accountRepository,
                               CustomerClient customerClient,
                               CurrentUser currentUser,
                               TransactionLedgerClient transactionLedgerClient) {
        this.accountRepository = accountRepository;
        this.customerClient = customerClient;
        this.currentUser = currentUser;
        this.transactionLedgerClient = transactionLedgerClient;
    }

    private String generateAccountNumber() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * Admins may act on any account. A customer may only act on accounts they
     * own. Deliberately reports "not found" rather than "forbidden" so a
     * customer cannot probe which account numbers exist on other customers.
     */
    private void assertCanAccess(Account account) {
        if (currentUser.isAdmin()) {
            return;
        }
        Long callerId = currentUser.getCustomerId();
        if (callerId == null || !callerId.equals(account.getCustomerId())) {
            throw new ResourceNotFoundException("Account not found: " + account.getAccountNumber());
        }
    }

    private void assertCanActForCustomer(Long customerId) {
        if (currentUser.isAdmin()) {
            return;
        }
        Long callerId = currentUser.getCustomerId();
        if (callerId == null || !callerId.equals(customerId)) {
            throw new ForbiddenException("You can only manage your own accounts");
        }
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
    @Transactional
    public AccountResponse createAccount(AccountRequest accountRequest) {
        assertCanActForCustomer(accountRequest.getCustomerId());

        CustomerDto customer = customerClient.getCustomerById(accountRequest.getCustomerId());

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(accountRequest.getAccountType())
                .balance(accountRequest.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .customerId(customer.getCustomerId())
                .build();

        Account savedAccount = accountRepository.save(account);

        // The opening deposit is money appearing on the account, so it belongs in
        // the ledger like any other movement. If it cannot be recorded we roll the
        // account back rather than open one whose balance nothing explains.
        if (savedAccount.getBalance() != null
                && savedAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            transactionLedgerClient.recordOpeningDeposit(
                    savedAccount.getAccountNumber(), savedAccount.getBalance());
        }

        return toResponse(savedAccount, customer);
    }

    @Override
    public AccountResponse getAccountById(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        assertCanAccess(account);

        CustomerDto customer = customerClient.getCustomerById(account.getCustomerId());

        return toResponse(account, customer);
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        // Admins see every account; a customer sees only their own.
        List<Account> accounts;
        if (currentUser.isAdmin()) {
            accounts = accountRepository.findAll();
        } else {
            Long callerId = currentUser.getCustomerId();
            accounts = callerId == null ? List.of() : accountRepository.findByCustomerId(callerId);
        }

        return accounts.stream()
                .map(account -> {
                    CustomerDto customer;
                    try {
                        customer = customerClient.getCustomerById(account.getCustomerId());
                    } catch (ResourceNotFoundException | ServiceUnavailableException ex) {
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
        assertCanAccess(account);
        // also stops a customer from re-assigning their account to someone else
        assertCanActForCustomer(accountRequest.getCustomerId());

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
        assertCanAccess(account);

        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public AccountBalanceResponse deposit(String accountNumber, DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        assertCanAccess(account);

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
        assertCanAccess(account);

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
        // Only the source account must be owned by the caller — you are allowed
        // to transfer money *to* anybody, but only *from* your own account.
        assertCanAccess(fromAccount);

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
