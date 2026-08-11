package com.bank.financialintelligence.service;

import com.bank.financialintelligence.dto.AIInsightResponse;
import com.bank.financialintelligence.dto.FinancialInsightResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // Simple Gemini connection test
    public String testGemini() {

        return chatClient
                .prompt()
                .user("Say hello and confirm that you are connected to my Spring Boot application.")
                .call()
                .content();
    }

    // Generate actual financial insights
    public AIInsightResponse generateFinancialInsights(
            FinancialInsightResponse financialData) {

        String prompt = """
                Analyze the following financial information for a bank customer.

                Account Number: %s

                Total Income: %s
                Total Expenses: %s
                Total Transfer In: %s
                Total Transfer Out: %s
                Net Cash Flow: %s
                Total Transactions: %s

                Spending By Category:
                %s

                Monthly Spending:
                %s

                Provide:
                1. A short summary of the customer's financial situation.
                2. Important financial insights or spending patterns.
                3. Practical recommendations.

                Do not invent information that is not present in the data.
                Keep the response concise.

                Format your response exactly as:

                SUMMARY:
                <summary>

                INSIGHTS:
                - <insight 1>
                - <insight 2>

                RECOMMENDATIONS:
                - <recommendation 1>
                - <recommendation 2>
                """.formatted(
                financialData.getAccountNumber(),
                financialData.getTotalIncome(),
                financialData.getTotalExpenses(),
                financialData.getTotalTransferIn(),
                financialData.getTotalTransferOut(),
                financialData.getNetCashFlow(),
                financialData.getTotalTransactions(),
                financialData.getSpendingByCategory(),
                financialData.getMonthlySpending()
        );

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        return parseResponse(
                financialData.getAccountNumber(),
                response
        );
    }

    private AIInsightResponse parseResponse(
            String accountNumber,
            String response) {

        String summary = "";
        String insightsText = "";
        String recommendationsText = "";

        if (response.contains("SUMMARY:")) {

            String[] parts = response.split(
                    "INSIGHTS:",
                    2
            );

            summary = parts[0]
                    .replace("SUMMARY:", "")
                    .trim();

            if (parts.length > 1) {

                String[] insightParts = parts[1].split(
                        "RECOMMENDATIONS:",
                        2
                );

                insightsText = insightParts[0].trim();

                if (insightParts.length > 1) {
                    recommendationsText =
                            insightParts[1].trim();
                }
            }
        }

        return AIInsightResponse.builder()
                .accountNumber(accountNumber)
                .summary(summary)
                .insights(toList(insightsText))
                .recommendations(toList(recommendationsText))
                .build();
    }

    private List<String> toList(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\n"))
                .map(String::trim)
                .map(line -> line.replaceFirst("^-\\s*", ""))
                .filter(line -> !line.isBlank())
                .toList();
    }
}