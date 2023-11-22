package com.example.order_management;

import com.example.order_management.dto.GoodInfoOrderDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.exception.NotEnoughGoodsException;
import com.example.order_management.models.Good;
import com.example.order_management.repository.OrderDetailRepository;
import com.example.order_management.service.GoodsService;
import com.example.order_management.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderCreationTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private GoodsService goodsService;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Test
    void create_new_order_test() {

        when(this.goodsService.getGoodIsNotSoldOut(anyLong())).thenReturn(stubGood(10));

        OrderDataDto orderDataDto = OrderDataDto.builder()
            .infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(1).quantityBuy(6).build()))
            .build();

        this.orderService.createNewOrder(orderDataDto);

        verify(this.orderDetailRepository, times(1)).saveAll(anyList());
    }

    @Test
    void create_new_order_with_more_goods_quantity_than_exist_in_goods_exception_test() {

        when(this.goodsService.getGoodIsNotSoldOut(anyLong())).thenReturn(stubGood(5));

        OrderDataDto orderDataDto = OrderDataDto.builder()
            .infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(1).quantityBuy(6).build()))
            .build();

        NotEnoughGoodsException exception = assertThrows(NotEnoughGoodsException.class, () -> this.orderService.createNewOrder(orderDataDto));

        assertThat(exception.getMessage(), equalTo("You want to buy more IPhone 10 than exists in stock!"));
    }

    private Good stubGood(int quantity) {
        return Good.builder().name("IPhone 10").price(BigDecimal.valueOf(12000)).quantity(quantity).build();
    }
}
