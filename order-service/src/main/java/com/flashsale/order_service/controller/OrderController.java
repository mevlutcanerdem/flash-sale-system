package com.flashsale.order_service.controller;


import com.flashsale.order_service.dto.OrderRequest;
import com.flashsale.order_service.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // it provides the send a message to kafka
    private final StockService stockService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderController(StockService stockService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.stockService = stockService;
        this.kafkaTemplate = kafkaTemplate;
    }
    // EKSİK OLAN METOD BURASI 👇
    @PostMapping("/init-stock")
    public ResponseEntity<String> initStock(@RequestParam String productId, @RequestParam int quantity) {
        stockService.initializeStock(productId, quantity);
        return ResponseEntity.ok(productId + " için " + quantity + " adet stok Redis'e yüklendi.");
    }


    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
            @RequestParam String productId,
            @RequestParam String userId,
            @RequestParam Double amount) {

        System.out.println("Yeni sipariş isteği geldi ! Ürün : "  + productId + " | Kullanıcı: " + userId) ;

        boolean isStockAvailable = stockService.decreaseStock(productId);

        if (!isStockAvailable)
            return ResponseEntity.badRequest().body("Üzgünüm, " + productId+  " stokları tükendi");

         // stock varsa kafkaya mesaj at (cüzdan servisi bunu duyup parayı kesecek)

        //  iki tarafı da stringe çevirmiştik bu yüzden json formatında basit string yolluyoruz
        String messagePayload = String.format("{\"userId\":\"%s\", \"amount\":%s}", userId, amount);


            kafkaTemplate.send("order-events",messagePayload);
            return ResponseEntity.ok("Sipariş kuyruğa alındı");


    }


}
