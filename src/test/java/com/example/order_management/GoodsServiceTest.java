package com.example.order_management;

import com.example.order_management.exception.GoodSoldOutException;
import com.example.order_management.models.Good;
import com.example.order_management.repository.GoodsRepository;
import com.example.order_management.service.GoodsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoodsServiceTest {

    @InjectMocks
    private GoodsServiceImpl goodsService;

    @Mock
    private GoodsRepository goodsRepository;

    @Test
    public void get_good_by_id_that_throw_entity_not_found_exception_test() {

        when(this.goodsRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> this.goodsService.getGoodIsNotSoldOut(anyLong()));

        assertThat(exception.getMessage(), equalTo("Good does not found!"));
    }

    @Test
    public void get_good_by_id_that_throw_sold_out_exception_test() {

        Good good = Good.builder().name("IPhone 10").quantity(0).price(BigDecimal.valueOf(23000)).build();
        when(this.goodsRepository.findById(anyLong())).thenReturn(Optional.of(good));

        GoodSoldOutException exception = assertThrows(GoodSoldOutException.class, () -> this.goodsService.getGoodIsNotSoldOut(anyLong()));

        assertThat(exception.getMessage(), equalTo("This IPhone 10 is sold out!"));
    }
}
