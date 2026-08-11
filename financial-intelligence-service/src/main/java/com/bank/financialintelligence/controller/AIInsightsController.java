package com.bank.financialintelligence.controller;

import com.bank.financialintelligence.dto.AIInsightResponse;
import com.bank.financialintelligence.dto.FinancialInsightResponse;
import com.bank.financialintelligence.dto.TransactionHistoryResponse;
import com.bank.financialintelligence.service.FinancialInsightService;
import com.bank.financialintelligence.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIInsightsController {

    private final FinancialInsightService financialInsightService;
    private final GeminiService geminiService;

    public AIInsightsController(
            FinancialInsightService financialInsightService,
            GeminiService geminiService) {

        this.financialInsightService = financialInsightService;
        this.geminiService = geminiService;
    }

    @GetMapping("/transactions/{accountNumber}")
    public List<TransactionHistoryResponse> getTransactions(
            @PathVariable String accountNumber) {

        return financialInsightService.getTransactions(accountNumber);
    }

    @GetMapping("/insights/{accountNumber}")
    public AIInsightResponse getInsights(
            @PathVariable String accountNumber) {

        FinancialInsightResponse financialData =
                financialInsightService.generateInsights(accountNumber);

        return geminiService.generateFinancialInsights(financialData);
    }
}