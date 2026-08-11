package com.bank.financialintelligence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIInsightResponse {

    private String accountNumber;

    private String summary;

    private List<String> insights;

    private List<String> recommendations;
}
