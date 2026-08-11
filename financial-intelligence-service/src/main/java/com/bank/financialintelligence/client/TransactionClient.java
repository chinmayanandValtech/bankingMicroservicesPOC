package com.bank.financialintelligence.client;

import com.bank.financialintelligence.dto.TransactionHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "transaction-service",
        url = "${transaction-service.url}"
)
public interface TransactionClient {

    @GetMapping("/api/transactions/{accountNumber}")
    List<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable("accountNumber") String accountNumber);
}