package com.example.order_management.repository;

import com.example.order_management.models.Good;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Good, Long> {
}
