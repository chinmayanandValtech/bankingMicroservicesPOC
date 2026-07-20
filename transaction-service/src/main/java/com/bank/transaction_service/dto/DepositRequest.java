package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {

 @NotBlank(message = "Account number is required")
 private String accountNumber;

 @NotNull(message = "Amount is required")
 @Positive(message = "Amount must be greater than zero")
 private BigDecimal amount;

 @Size(max = 255)
 private String remarks;
}
