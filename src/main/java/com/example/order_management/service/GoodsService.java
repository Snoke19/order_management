package com.example.order_management.service;

import com.example.order_management.models.Good;

import java.util.List;

public interface GoodsService {

    List<Good> getAllByAvailableGoods();
    void addGood(Good good);
    Good getGoodIsNotSoldOut(long idGood);
}
