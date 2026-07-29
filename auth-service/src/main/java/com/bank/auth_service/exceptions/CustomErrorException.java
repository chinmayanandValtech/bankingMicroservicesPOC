package com.bank.auth_service.exceptions;

public class CustomErrorException extends RuntimeException {
    public CustomErrorException(String message) {
        super(message);
    }
}
