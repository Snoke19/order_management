package com.example.order_management;

import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.GoodInfoOrderDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.dto.PaymentInfoDto;
import com.example.order_management.enums.OrderStatus;
import com.example.order_management.exception.GoodSoldOutException;
import com.example.order_management.models.Good;
import com.example.order_management.models.Order;
import com.example.order_management.models.OrderDetail;
import com.example.order_management.repository.GoodsRepository;
import com.example.order_management.repository.OrderDetailRepository;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles(value = "test")
public class OrderIntegrationTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
        .withInitScript("./sql_scripts/schema_without_data.sql");

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

        //given
        Good goodSaved = goodsRepository.save(stubGood());

        //act
        this.orderService.createNewOrder(stubOrderDataDto(goodSaved));

        //assert
        List<FullInfoOrdersDto> orders = this.orderService.getAllOrders();

        assertAll(
            () -> assertThat(orders.size(), equalTo(1)),
            () -> assertThat(orders.get(0).getStatus(), equalTo(OrderStatus.PROCESSING)),
            () -> assertThat(orders.get(0).getCreated(), notNullValue()),
            () -> assertThat(orders.get(0).getOrderGoodDetails(), not(empty())),

            () -> assertThat(orders.get(0).getOrderGoodDetails().get(0).getName(), equalTo("IPhone 10")),
            () -> assertThat(orders.get(0).getOrderGoodDetails().get(0).getPrice(), equalTo(BigDecimal.valueOf(24000))),
            () -> assertThat(orders.get(0).getOrderGoodDetails().get(0).getQuantityBuy(), equalTo(2))
        );

        List<Good> goods = this.goodsRepository.findAll();

        assertThat(goods.get(0).getQuantity(), equalTo(3));
    }

    @Test
    void create_new_order_when_good_quantity_is_zero_in_stock_exception_test() {

        Good good = Good.builder().name("IPhone 10").quantity(0).price(BigDecimal.valueOf(23000)).build();
        Good goodSaved = goodsRepository.save(good);

        GoodSoldOutException exception = assertThrows(GoodSoldOutException.class, () -> this.orderService.createNewOrder(stubOrderDataDto(goodSaved)));

        List<Order> orders = this.orderRepository.findAll();
        List<OrderDetail> orderDetails = this.orderDetailRepository.findAll();

        assertThat(exception.getMessage(), equalTo("This IPhone 10 is sold out!"));
        assertTrue(orders.isEmpty());
        assertTrue(orderDetails.isEmpty());
    }

    @Test
    void create_new_order_when_good_that_does_not_exist_exception_test() {

        Good good = Good.builder().id(Long.MAX_VALUE).build();
        OrderDataDto orderDataDto = stubOrderDataDto(good);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> this.orderService.createNewOrder(orderDataDto));

        List<Order> orders = this.orderRepository.findAll();
        List<OrderDetail> orderDetails = this.orderDetailRepository.findAll();

        assertThat(exception.getMessage(), equalTo("Good does not found!"));
        assertTrue(orders.isEmpty());
        assertTrue(orderDetails.isEmpty());
    }

    @Test
    void pay_for_order_test() {
        Good goodSaved = goodsRepository.save(stubGood());
        this.orderService.createNewOrder(stubOrderDataDto(goodSaved));
        List<FullInfoOrdersDto> orders = this.orderService.getAllOrders();

        this.orderService.payForOrder(PaymentInfoDto.builder().orderId(orders.get(0).getOrderId()).build());

        List<FullInfoOrdersDto> ordersNew = this.orderService.getAllOrders();

        assertThat(ordersNew.get(0).getStatus(), equalTo(OrderStatus.PAID));
        assertThat(ordersNew.get(0).getOrderGoodDetails().get(0).getName(), equalTo(goodSaved.getName()));
        assertThat(ordersNew.get(0).getOrderGoodDetails().get(0).getQuantityBuy(), equalTo(2));

        Optional<Good> goodOptional = this.goodsRepository.findById(goodSaved.getId());

        if (goodOptional.isPresent()) {
            Good good = goodOptional.get();
            assertThat(good.getQuantity(), equalTo(3));
        }
    }

    private OrderDataDto stubOrderDataDto(Good good) {
        return OrderDataDto.builder().infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(good.getId()).quantityBuy(2).build())).build();
    }

    private Good stubGood() {
        return Good.builder().name("IPhone 10").price(BigDecimal.valueOf(12000)).quantity(5).build();
    }
}
