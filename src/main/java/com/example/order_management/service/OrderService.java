package com.example.order_management.service;

import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.OrderDataDto;

import java.util.List;

public interface OrderService {

    List<FullInfoOrdersDto> getAllOrders();
    void createNewOrder(OrderDataDto orderData);
    void payForOrder(long orderId);

    void deleteUnpaidOrders();
}
