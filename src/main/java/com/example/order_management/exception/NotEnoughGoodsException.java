package com.example.order_management.exception;

public class NotEnoughGoodsException extends RuntimeException {

    public NotEnoughGoodsException(String message) {
        super(message);
    }
}
