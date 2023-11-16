package com.example.order_management.repository;

import com.example.order_management.models.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

    public interface GoodsRepository extends JpaRepository<Good, Long> {

        @Query("select g from Good g where g.quantity > 0")
        List<Good> findAllByAvailableGoods();
    }
