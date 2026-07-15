package com.bank.account_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long customerId;

    private String firstName;

    private String lastName;
}
