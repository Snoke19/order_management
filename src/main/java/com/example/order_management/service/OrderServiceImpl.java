package com.example.order_management.service;

import com.example.order_management.dto.FullInfoOrdersDto;
import com.example.order_management.dto.GoodInfoOrderDto;
import com.example.order_management.dto.OrderDataDto;
import com.example.order_management.dto.OrderGoodDetailsDto;
import com.example.order_management.dto.PaymentInfoDto;
import com.example.order_management.enums.OrderStatus;
import com.example.order_management.exception.NotEnoughGoodsException;
import com.example.order_management.models.Good;
import com.example.order_management.models.Order;
import com.example.order_management.models.OrderDetail;
import com.example.order_management.repository.OrderDetailRepository;
import com.example.order_management.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final GoodsService goodsService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderServiceImpl(GoodsService goodsService, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.goodsService = goodsService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    @Transactional
    public List<FullInfoOrdersDto> getAllOrders() {
        return this.orderRepository.findAll().stream().map(data -> FullInfoOrdersDto.builder()
            .orderId(data.getIdOrder())
            .created(data.getCreated())
            .status(data.getStatus())
            .orderGoodDetails(data.getOrderDetails().stream().map(data1 -> {
                Good good = data1.getGood();
                return OrderGoodDetailsDto.builder()
                    .goodId(good.getId())
                    .name(good.getName())
                    .price(good.getPrice().multiply(BigDecimal.valueOf(data1.getQuantityBuy())))
                    .quantityBuy(data1.getQuantityBuy())
                    .build();
            }).collect(Collectors.toList()))
            .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createNewOrder(OrderDataDto orderData) {

        Order order = Order.builder().created(LocalDateTime.now()).status(OrderStatus.PROCESSING).build();

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<GoodInfoOrderDto> goodInfoOrders = orderData.getInfoGoodOrders();

        for (GoodInfoOrderDto goodInfoOrderDto : goodInfoOrders) {
            Good good = this.goodsService.getGoodIsNotSoldOut(goodInfoOrderDto.getIdGood());

            int enoughGoodsItems = good.getQuantity() - goodInfoOrderDto.getQuantityBuy();

            if (enoughGoodsItems < 0) {
                throw new NotEnoughGoodsException("You want to buy more " + good.getName() + " than exists in stock!");
            }

            good.setQuantity(enoughGoodsItems);

            OrderDetail orderDetail = OrderDetail.builder()
                .good(good)
                .quantityBuy(goodInfoOrderDto.getQuantityBuy())
                .order(order)
                .build();

            orderDetails.add(orderDetail);

        }

        this.orderDetailRepository.saveAll(orderDetails);
    }

    @Override
    @Transactional
    public void payForOrder(PaymentInfoDto paymentInfo) {

        Order order = this.orderRepository.findById(paymentInfo.getOrderId()).orElseThrow(() -> new EntityNotFoundException("Order is not found!"));

        order.setStatus(OrderStatus.PAID);
    }

    @Override
    @Transactional
    public void deleteUnpaidOrders() {

        List<Order> orders = this.orderRepository.findOrderByStatusAndCreated(OrderStatus.PROCESSING, LocalDateTime.now());

        this.orderRepository.deleteAllById(orders.stream().map(Order::getIdOrder).collect(Collectors.toList()));

        List<OrderDetail> orderDetails = orders.stream().flatMap(data -> data.getOrderDetails().stream()).toList();

        for (OrderDetail orderDetail : orderDetails) {
            Good good = orderDetail.getGood();
            good.setQuantity(good.getQuantity() + orderDetail.getQuantityBuy());
            this.goodsService.addGood(good);
        }
    }
}
