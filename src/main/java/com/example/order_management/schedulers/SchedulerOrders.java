package com.example.order_management.schedulers;

import com.example.order_management.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerOrders {

    private final OrderService orderService;

    public SchedulerOrders(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRateString = "${scheduler.delete.unpaid.order.time}")
    public void deleteUnpaidOrders() {
        log.info("start delete unpaid orders");
        this.orderService.deleteUnpaidOrders();
    }
}