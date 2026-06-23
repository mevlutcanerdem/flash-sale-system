package com.flashsale.order_service.repository;

import com.flashsale.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository  extends JpaRepository<Order,Long> {

    // Gelecekte orderNumber üzerinden sipariş bulabilmek için özel metod
    Optional<Order> findByOrderNumber(String orderNumber);
}

