package com.example.order_management;

import com.example.order_management.dto.PaymentInfoDto;
import com.example.order_management.enums.OrderStatus;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.service.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderPayTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void pay_for_order_that_already_paid_or_not_exist_exception_test() {

        when(this.orderRepository.findByIdOrderAndStatus(anyLong(), any(OrderStatus.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            this.orderService.payForOrder(PaymentInfoDto.builder().orderId(42423).build()));

        assertThat(exception.getMessage(), equalTo("Order is paid or not found!"));
        verify(this.orderRepository, times(1)).findByIdOrderAndStatus(anyLong(), any(OrderStatus.class));
    }
}
