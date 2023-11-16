package com.example.order_management.controllers;

import com.example.order_management.dto.GoodDto;
import com.example.order_management.dto.InfoGoodDto;
import com.example.order_management.mapper.GoodsDtoMapper;
import com.example.order_management.models.Good;
import com.example.order_management.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {

    private final GoodsService goodsService;
    private final GoodsDtoMapper goodsDtoMapper;

    public GoodsController(GoodsService goodsService, GoodsDtoMapper goodsDtoMapper) {
        this.goodsService = goodsService;
        this.goodsDtoMapper = goodsDtoMapper;
    }

    @GetMapping("/goods")
    private List<InfoGoodDto> getAllByAvailableGoods() {
        return goodsDtoMapper.entityToDto(this.goodsService.getAllByAvailableGoods());
    }

    @PostMapping("/good")
    private void addNewGood(@RequestBody @Validated GoodDto goodDto) {
        Good good = this.goodsDtoMapper.dtoToEntity(goodDto);

        this.goodsService.addGood(good);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
