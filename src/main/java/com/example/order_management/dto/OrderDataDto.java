package com.example.order_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDataDto {

    @NotNull(message = "Add please orders details!")
    private List<GoodInfoOrderDto> infoGoodOrders;
}
