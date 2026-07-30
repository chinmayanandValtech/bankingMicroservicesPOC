package com.bank.transaction_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank
    private String fromAccountNumber;

    @NotBlank
    private String toAccountNumber;

    @NotNull
    @Positive
    private BigDecimal amount;

    @Size(max = 255)
    private String remarks;
}
