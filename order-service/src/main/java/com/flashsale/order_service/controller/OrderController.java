package com.flashsale.order_service.controller;


import com.flashsale.order_service.dto.OrderRequest;
import com.flashsale.order_service.dto.PaymentFailedEvent;
import com.flashsale.order_service.listener.PaymentFailedListener;
import com.flashsale.order_service.service.OrderManagerService;
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
    private final OrderManagerService orderManagerService;
    private final PaymentFailedListener paymentFailedListener;
    public OrderController(StockService stockService, OrderManagerService orderManagerService, PaymentFailedListener paymentFailedListener) {
        this.stockService = stockService;
        this.orderManagerService = orderManagerService;
        this.paymentFailedListener = paymentFailedListener;
    }

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

            try {
                // tüm business logic i servise devrettik
                String orderNumber = orderManagerService.processOrder(productId,userId,amount);
                return ResponseEntity.ok("Sipariş alındı! Sipariş no: "  + orderNumber);
            }catch (Exception e){
                // stock biterse hata döneceğiz servis bize hata fırlatacak biz de 400 döncez
                return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
            }


    }
    // --- KAFKA OLMADAN SAGA TEST ENDPOINT'İ ---
    @PostMapping("/test-rollback")
    public ResponseEntity<String> testRollback(@RequestParam String orderNumber) {
        // Sanki cüzdandan "param yok" mesajı gelmiş gibi sahte bir olay yaratıyoruz
        PaymentFailedEvent fakeEvent = new PaymentFailedEvent(orderNumber, "test_user", "Manuel Postman Testi");

        // Kafka'yı beklemeden metodu doğrudan tetikliyoruz!
        paymentFailedListener.handlePaymentFailed(fakeEvent);

        return ResponseEntity.ok("Geri alma metodu tetiklendi! Konsolu kontrol et.");
    }


}
