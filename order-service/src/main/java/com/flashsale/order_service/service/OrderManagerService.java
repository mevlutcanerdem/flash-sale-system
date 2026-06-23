package com.flashsale.order_service.service;


import com.flashsale.order_service.dto.OrderEvent;
import com.flashsale.order_service.model.Order;
import com.flashsale.order_service.model.OrderStatus;
import com.flashsale.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderManagerService {
        private final StockService stockService;
        private final OrderRepository orderRepository;
        private final KafkaTemplate<String,Object> kafkaTemplate;


    public OrderManagerService(StockService stockService, OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.stockService = stockService;
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    public String processOrder(String productId,String userId,Double amount) {
        // ışık hızında stok kontrolü ve düşümü
        boolean isStockAvailable = stockService.decreaseStock(productId);
        if (!isStockAvailable){
                throw new RuntimeException(productId + " stokları tükendi");
        }

        // takip edilebilir benzersiz bir sipariş numarası üret
        String orderNumber = UUID.randomUUID().toString();

        //  siparişi veritabanına PENDING durumuyla kaydet
        Order newOrder = new Order();
        newOrder.setOrderNumber(orderNumber);
        newOrder.setUserId(userId);
        newOrder.setProductId(productId);
        newOrder.setAmount(amount);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setCreatedAt(LocalDateTime.now());

        orderRepository.save(newOrder);
        System.out.println("Sipariş veritabanına kaydedildi.Durum : PENDING.Sipariş no :  " + orderNumber);

        // Kafkaya asenkron bir mesaj gönder
        OrderEvent orderEvent = new OrderEvent(orderNumber,userId,amount);
        // Objemizi orderEvent doğrudan fırlatıyoruz .Spring kafka ayarlarından dolayı onu otmatik JSON yapacak
        kafkaTemplate.send("order-events-v2",orderEvent);
        System.out.println("Kafkaya DTO(orderEvent) fırlatıldı! ");
        // işlem bittiğinde müşteriye sipariş numarasını veriyoruz
        return orderNumber;
    }
}
