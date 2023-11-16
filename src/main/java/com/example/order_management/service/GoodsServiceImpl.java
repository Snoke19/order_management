package com.example.order_management.service;

import com.example.order_management.exception.GoodSoldOutException;
import com.example.order_management.models.Good;
import com.example.order_management.repository.GoodsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;

    public GoodsServiceImpl(GoodsRepository goodsRepository) {
        this.goodsRepository = goodsRepository;
    }

    @Override
    public List<Good> getAllGoods() {
        return this.goodsRepository.findAll();
    }

    @Override
    @Transactional
    public void addGood(Good good) {
        this.goodsRepository.save(good);
    }

    @Override
    public Good getGoodIsNotSoldOut(long idGood) {

        Good good = this.goodsRepository.findById(idGood).orElseThrow(() -> {
            log.info("EntityNotFoundException: {}, idGood: {}", "Good does not found!", idGood);
            return new GoodSoldOutException("Good does not found!");
        });

        if (good.getQuantity() == 0) {
            String message = "This " + good.getName() + " is sold out!";
            log.info("GoodSoldOutException: {}, idGood: {}", message, idGood);
            throw new GoodSoldOutException(message);
        }

        return good;
    }
}
