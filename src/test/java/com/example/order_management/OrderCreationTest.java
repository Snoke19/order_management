package com.example.order_management;

import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.GoodInfoOrderDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.enums.OrderStatus;
import com.example.order_management.exception.GoodSoldOutException;
import com.example.order_management.exception.NotEnoughGoodsException;
import com.example.order_management.models.Good;
import com.example.order_management.repository.GoodsRepository;
import com.example.order_management.repository.OrderDetailRepository;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
public class OrderCreationTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
        .withInitScript("database.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void beforeEachTest() {
        this.orderDetailRepository.deleteAll();
        this.goodsRepository.deleteAll();
        this.orderRepository.deleteAll();
    }

    @Test
    void create_new_order_with_one_goods_test() {

        Good goodSaved = goodsRepository.save(stubGood());

        this.orderService.createNewOrder(stubOrderDataDto(goodSaved));

        List<FullInfoOrdersDto> orders = this.orderService.getAllOrders();

        assertThat(orders.size(), equalTo(1));
        assertThat(orders.get(0).getStatus(), equalTo(OrderStatus.PROCESSING));
        assertThat(orders.get(0).getCreated(), notNullValue());
        assertThat(orders.get(0).getOrderGoodDetails(), not(empty()));

        assertThat(orders.get(0).getOrderGoodDetails().get(0).getName(), equalTo("IPhone 10"));
        assertThat(orders.get(0).getOrderGoodDetails().get(0).getPrice(), equalTo(BigDecimal.valueOf(12000)));
        assertThat(orders.get(0).getOrderGoodDetails().get(0).getQuantityBuy(), equalTo(2));

        List<Good> goods = this.goodsRepository.findAll();

        assertThat(goods.get(0).getQuantity(), equalTo(3));
    }

    @Test
    void create_new_order_with_more_goods_quantity_than_exist_in_goods_test() {

        Good goodSaved = goodsRepository.save(stubGood());

        OrderDataDto orderDataDto = OrderDataDto.builder()
            .infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(goodSaved.getId()).quantityBuy(6).build()))
            .build();

        NotEnoughGoodsException exception = assertThrows(NotEnoughGoodsException.class, () -> this.orderService.createNewOrder(orderDataDto));

        assertThat(exception.getMessage(), equalTo("You want to buy more IPhone 10 than exists in stock!"));
    }

    @Test
    void create_new_order_when_good_quantity_is_zero_in_stock_test() {

        Good good = Good.builder().name("IPhone 10").quantity(0).build();
        Good goodSaved = goodsRepository.save(good);

        GoodSoldOutException exception = assertThrows(GoodSoldOutException.class, () -> this.orderService.createNewOrder(stubOrderDataDto(goodSaved)));

        assertThat(exception.getMessage(), equalTo("This IPhone 10 is sold out!"));
    }

    @Test
    void create_new_order_when_good_that_does_not_exist_test() {

        GoodSoldOutException exception = assertThrows(GoodSoldOutException.class, () ->
            this.orderService.createNewOrder(stubOrderDataDto(Good.builder().id(334L).build())));

        assertThat(exception.getMessage(), equalTo("Good does not found!"));
    }

    @Test
    void pay_for_order_test() {
        Good goodSaved = goodsRepository.save(stubGood());
        this.orderService.createNewOrder(stubOrderDataDto(goodSaved));
        List<FullInfoOrdersDto> orders = this.orderService.getAllOrders();

        this.orderService.payForOrder(orders.get(0).getOrderId());

        List<FullInfoOrdersDto> ordersNew = this.orderService.getAllOrders();

        assertThat(ordersNew.get(0).getStatus(), equalTo(OrderStatus.PAID));
    }

    @Test
    void pay_for_order_that_already_deleted_test() {

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> this.orderService.payForOrder(42423));

        assertThat(exception.getMessage(), equalTo("Order is not found!"));
    }

    private OrderDataDto stubOrderDataDto(Good good) {
        return OrderDataDto.builder().infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(good.getId()).quantityBuy(2).build())).build();
    }

    private Good stubGood() {
        return Good.builder().name("IPhone 10").price(BigDecimal.valueOf(12000)).quantity(5).build();
    }
}
