package com.bank.financialintelligence.service;

import com.bank.financialintelligence.client.TransactionClient;
import com.bank.financialintelligence.dto.FinancialInsightResponse;
import com.bank.financialintelligence.dto.MonthlySpending;
import com.bank.financialintelligence.dto.SpendingCategory;
import com.bank.financialintelligence.dto.TransactionHistoryResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialInsightService {

    private final TransactionClient transactionClient;
    private final SpendingCategoryService spendingCategoryService;

    public FinancialInsightService(
            TransactionClient transactionClient,
            SpendingCategoryService spendingCategoryService) {

        this.transactionClient = transactionClient;
        this.spendingCategoryService = spendingCategoryService;
    }

    public List<TransactionHistoryResponse> getTransactions(String accountNumber) {
        return transactionClient.getTransactionHistory(accountNumber);
    }

    public FinancialInsightResponse generateInsights(String accountNumber) {

        List<TransactionHistoryResponse> transactions =
                transactionClient.getTransactionHistory(accountNumber);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal totalTransferIn = BigDecimal.ZERO;
        BigDecimal totalTransferOut = BigDecimal.ZERO;

        Map<SpendingCategory, BigDecimal> spendingByCategory =
                new EnumMap<>(SpendingCategory.class);

        for (TransactionHistoryResponse transaction : transactions) {

            BigDecimal amount = transaction.getAmount();

            switch (transaction.getTransactionType()) {

                case "DEPOSIT":
                    totalIncome = totalIncome.add(amount);
                    break;

                case "WITHDRAWAL":

                    totalExpenses = totalExpenses.add(amount);

                    SpendingCategory category =
                            spendingCategoryService.categorize(
                                    transaction.getRemarks());

                    spendingByCategory.merge(
                            category,
                            amount,
                            BigDecimal::add
                    );

                    break;

                case "TRANSFER":

                    if (accountNumber.equals(transaction.getToAccountNumber())) {
                        totalTransferIn = totalTransferIn.add(amount);
                    }

                    if (accountNumber.equals(transaction.getFromAccountNumber())) {
                        totalTransferOut = totalTransferOut.add(amount);
                    }

                    break;
            }
        }

        List<MonthlySpending> monthlySpending =
                calculateMonthlySpending(accountNumber, transactions);

        BigDecimal netCashFlow =
                totalIncome
                        .add(totalTransferIn)
                        .subtract(totalExpenses)
                        .subtract(totalTransferOut);

        return FinancialInsightResponse.builder()
                .accountNumber(accountNumber)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .totalTransferIn(totalTransferIn)
                .totalTransferOut(totalTransferOut)
                .netCashFlow(netCashFlow)
                .totalTransactions(transactions.size())
                .spendingByCategory(spendingByCategory)
                .monthlySpending(monthlySpending)
                .build();
    }

    private List<MonthlySpending> calculateMonthlySpending(
            String accountNumber,
            List<TransactionHistoryResponse> transactions) {

        Map<YearMonth, BigDecimal> monthlyIncome = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> monthlyExpenses = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> monthlyTransferIn = new LinkedHashMap<>();
        Map<YearMonth, BigDecimal> monthlyTransferOut = new LinkedHashMap<>();

        for (TransactionHistoryResponse transaction : transactions) {

            if (transaction.getTransactionTime() == null) {
                continue;
            }

            YearMonth month =
                    YearMonth.from(transaction.getTransactionTime());

            switch (transaction.getTransactionType()) {

                case "DEPOSIT":
                    monthlyIncome.merge(
                            month,
                            transaction.getAmount(),
                            BigDecimal::add
                    );
                    break;

                case "WITHDRAWAL":
                    monthlyExpenses.merge(
                            month,
                            transaction.getAmount(),
                            BigDecimal::add
                    );
                    break;

                case "TRANSFER":

                    if (accountNumber.equals(transaction.getToAccountNumber())) {
                        monthlyTransferIn.merge(
                                month,
                                transaction.getAmount(),
                                BigDecimal::add
                        );
                    }

                    if (accountNumber.equals(transaction.getFromAccountNumber())) {
                        monthlyTransferOut.merge(
                                month,
                                transaction.getAmount(),
                                BigDecimal::add
                        );
                    }

                    break;
            }
        }

        // Collect every month that has financial activity
        Map<YearMonth, MonthlySpending> monthlyData =
                new LinkedHashMap<>();

        monthlyIncome.keySet().forEach(month ->
                monthlyData.putIfAbsent(month, null)
        );

        monthlyExpenses.keySet().forEach(month ->
                monthlyData.putIfAbsent(month, null)
        );

        monthlyTransferIn.keySet().forEach(month ->
                monthlyData.putIfAbsent(month, null)
        );

        monthlyTransferOut.keySet().forEach(month ->
                monthlyData.putIfAbsent(month, null)
        );

        List<MonthlySpending> result = new ArrayList<>();

        for (YearMonth month : monthlyData.keySet()) {

            BigDecimal income =
                    monthlyIncome.getOrDefault(
                            month,
                            BigDecimal.ZERO
                    );

            BigDecimal expenses =
                    monthlyExpenses.getOrDefault(
                            month,
                            BigDecimal.ZERO
                    );

            BigDecimal transferIn =
                    monthlyTransferIn.getOrDefault(
                            month,
                            BigDecimal.ZERO
                    );

            BigDecimal transferOut =
                    monthlyTransferOut.getOrDefault(
                            month,
                            BigDecimal.ZERO
                    );

            BigDecimal netCashFlow =
                    income
                            .add(transferIn)
                            .subtract(expenses)
                            .subtract(transferOut);

            result.add(
                    MonthlySpending.builder()
                            .month(month.toString())
                            .totalIncome(income)
                            .totalExpenses(expenses)
                            .totalTransferIn(transferIn)
                            .totalTransferOut(transferOut)
                            .netCashFlow(netCashFlow)
                            .build()
            );
        }

        return result;
    }
}