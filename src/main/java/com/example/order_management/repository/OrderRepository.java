package com.example.order_management.repository;

import com.example.order_management.enums.OrderStatus;
import com.example.order_management.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o left join o.orderDetails where o.status = :status and o.created < :currentTime")
    List<Order> findOrderByStatusAndCreated(@Param("status") OrderStatus status, @Param("currentTime") LocalDateTime currentTime);

    Optional<Order> findByIdOrderAndStatus(long orderId, OrderStatus status);
}
