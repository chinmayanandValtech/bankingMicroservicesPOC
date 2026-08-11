package com.bank.financialintelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInsightResponse {

    private String accountNumber;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal totalTransferIn;

    private BigDecimal totalTransferOut;

    private BigDecimal netCashFlow;

    private int totalTransactions;

    private Map<SpendingCategory, BigDecimal> spendingByCategory;

    private List<MonthlySpending> monthlySpending;
}