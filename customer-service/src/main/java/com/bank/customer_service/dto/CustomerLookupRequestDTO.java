package com.bank.customer_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLookupRequestDTO {

    @NotNull
    private Long customerId;
}
