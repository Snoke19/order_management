package com.example.order_management.exception;

public class GoodSoldOutException extends RuntimeException {

    public GoodSoldOutException(String message) {
        super(message);
    }
}
