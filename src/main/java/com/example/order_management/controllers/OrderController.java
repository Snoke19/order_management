package com.example.order_management.controllers;

import com.example.order_management.ErrorMessage;
import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.exception.GoodSoldOutException;
import com.example.order_management.exception.NotEnoughGoodsException;
import com.example.order_management.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<FullInfoOrdersDto> getOrders() {
        return this.orderService.getAllOrders();
    }

    @PostMapping("/order")
    public void createNewOrder(@RequestBody OrderDataDto orderData) {
        this.orderService.createNewOrder(orderData);
    }

    @PostMapping("/order/pay")
    public void payForOrder(@RequestBody Long orderId) {
        this.orderService.payForOrder(orderId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorMessage handleValidationExceptions(EntityNotFoundException ex) {
        return ErrorMessage.builder().errorCode("entityNotFoundException").error(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GoodSoldOutException.class)
    public ErrorMessage godSoldOutException(GoodSoldOutException ex) {
        return ErrorMessage.builder().errorCode("goodSoldOutException").error(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughGoodsException.class)
    public ErrorMessage notEnoughGoodsException(NotEnoughGoodsException ex) {
        return ErrorMessage.builder().errorCode("notEnoughGoodsException").error(ex.getMessage()).build();
    }
}
