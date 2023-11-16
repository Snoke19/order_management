package com.example.order_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodDto {

    @NotBlank(message = "Add please name of the good!")
    private String name;

    @NotBlank(message = "Add please description about the good!")
    private String description;

    @Min(value = 1, message = "Price cannot be less than zero!")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity cannot be less than zero!")
    private int quantity;
}
