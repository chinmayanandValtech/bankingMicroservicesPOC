package com.bank.account_service.dto;

import com.bank.account_service.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private AccountType accountType;

    @NotNull
    @PositiveOrZero
    private BigDecimal initialDeposit;
}
