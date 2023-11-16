package com.example.order_management;


import com.example.order_management.models.Good;
import com.example.order_management.repository.GoodsRepository;
import com.example.order_management.service.GoodsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles(value = "test")
public class GoodsIntegrationTest {

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
    private GoodsService goodsService;

    @Autowired
    private GoodsRepository goodsRepository;

    @BeforeEach
    public void beforeEachTest() {
        this.goodsRepository.deleteAll();
    }

    @Test
    public void get_all_available_goods_test() {

        //given
        this.goodsRepository.save(stubGood("IPhone 11", 5));
        this.goodsRepository.save(stubGood("IPhone 12", 5));
        this.goodsRepository.save(stubGood("IPhone 13", 0));

        //act
        List<Good> goodList = this.goodsService.getAllByAvailableGoods();

        //assert
        assertThat(goodList.size(), equalTo(2));
        assertThat(goodList.get(0).getName(), equalTo("IPhone 11"));
        assertThat(goodList.get(1).getName(), equalTo("IPhone 12"));
    }

    @Test
    public void add_good_test() {

        this.goodsService.addGood(Good.builder().name("test").description("test_desc").price(BigDecimal.valueOf(12000)).quantity(2).build());

        List<Good> goods = this.goodsService.getAllByAvailableGoods();

        assertThat(goods.size(), equalTo(1));
        assertThat(goods.get(0).getName(), equalTo("test"));
    }

    @Test
    public void add_good_with_null_properties_exception_test() {

        DataIntegrityViolationException dataAccessException = assertThrows(DataIntegrityViolationException.class, () ->
            this.goodsService.addGood(Good.builder().name(null).description(null).price(BigDecimal.valueOf(12000)).quantity(2).build()));

        assertThat(dataAccessException.getMostSpecificCause().getMessage(), equalTo("Column 'name' cannot be null"));
    }

    @Test
    public void add_goods_with_the_same_name_exception_test() {

        DataIntegrityViolationException dataAccessException = assertThrows(DataIntegrityViolationException.class, () -> {
            this.goodsService.addGood(stubGood("IPhone 11", 2));
            this.goodsService.addGood(stubGood("IPhone 11", 1));
        });

        assertThat(dataAccessException.getMostSpecificCause().getMessage(), equalTo("Duplicate entry 'IPhone 11' for key 'goods_name_pk'"));
    }

    @Test
    public void add_good_with_null_entity_exception_test1() {

        InvalidDataAccessApiUsageException dataAccessException = assertThrows(InvalidDataAccessApiUsageException.class, () ->
            this.goodsService.addGood(null));

        assertThat(dataAccessException.getMostSpecificCause().getMessage(), equalTo("Entity must not be null"));
    }

    private Good stubGood(String name, int quantity) {
        return Good.builder().name(name).price(BigDecimal.valueOf(12000)).quantity(quantity).build();
    }
}
