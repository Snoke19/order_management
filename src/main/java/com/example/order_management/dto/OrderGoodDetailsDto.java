package com.example.order_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderGoodDetailsDto {

    private long goodId;
    private String name;
    private BigDecimal price;
    private int quantityBuy;
}
