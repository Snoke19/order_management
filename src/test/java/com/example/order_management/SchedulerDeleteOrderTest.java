package com.example.order_management;

import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.GoodInfoOrderDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.models.Good;
import com.example.order_management.repository.GoodsRepository;
import com.example.order_management.service.OrderService;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest
@ActiveProfiles(value = "test")
public class SchedulerDeleteOrderTest {

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

    @Test
    public void delete_unpaid_orders_test() {

        Good goodSaved = this.goodsRepository.save(stubGood());
        this.orderService.createNewOrder(stubOrderDataDto(goodSaved));

        this.orderService.deleteUnpaidOrders();

        List<FullInfoOrdersDto> orders = this.orderService.getAllOrders();
        assertThat(orders.size(), equalTo(0));
    }

    private Good stubGood() {
        return Good.builder().name("IPhone 10").price(BigDecimal.valueOf(12000)).quantity(5).build();
    }

    private OrderDataDto stubOrderDataDto(Good good) {
        return OrderDataDto.builder().infoGoodOrders(List.of(GoodInfoOrderDto.builder().idGood(good.getId()).quantityBuy(2).build())).build();
    }
}
