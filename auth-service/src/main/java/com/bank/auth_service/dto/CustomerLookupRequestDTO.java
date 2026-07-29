package com.bank.auth_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLookupRequestDTO {

    private Long customerId;
}
