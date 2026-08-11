package com.bank.financialintelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinancialIntelligenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialIntelligenceServiceApplication.class, args);
    }
}