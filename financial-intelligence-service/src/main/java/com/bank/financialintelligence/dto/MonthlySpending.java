package com.bank.financialintelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySpending {

    private String month;

    private BigDecimal totalExpenses;

    private BigDecimal totalIncome;

    private BigDecimal netCashFlow;

    private BigDecimal totalTransferIn;

    private BigDecimal totalTransferOut;
}