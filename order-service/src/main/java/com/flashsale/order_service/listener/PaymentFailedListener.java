package com.flashsale.order_service.listener;


import com.flashsale.order_service.dto.PaymentFailedEvent;
import com.flashsale.order_service.model.Order;
import com.flashsale.order_service.model.OrderStatus;
import com.flashsale.order_service.repository.OrderRepository;
import com.flashsale.order_service.service.StockService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentFailedListener {

    private final OrderRepository orderRepository;
    private final StockService stockService;

    public PaymentFailedListener(OrderRepository orderRepository, StockService stockService) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
    }

    @KafkaListener(topics = "payment-failed-events-v2",groupId = "order-group-v2")
    public void handlePaymentFailed(PaymentFailedEvent event){
        System.out.println("------------------");
        System.out.println("SAGA geri alma(ROLLBACK) : ODEME BAŞARISIZ FİŞEĞİ YAKALANDI");
        System.out.println("Sipariş no : " + event.orderNumber());
        System.out.println("Kullanıcı: " + event.userId());
        System.out.println("Neden : " + event.reason());
        System.out.println("--------------------");

        Optional<Order> orderOpt= orderRepository.findByOrderNumber(event.orderNumber());
        if (orderOpt.isPresent()){
            Order order = orderOpt.get();
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            System.out.println("Veritabanı: Sipariş durumu failed olarak güncellendi.");

            stockService.initializeStock(order.getProductId(),1);
            System.out.println("Redis : Eksilen 1 adet stok " + order.getProductId() + " için başarıyla iade edildi!");

        }
        else {
            System.out.println("Hata: sipariş numarası veritabanında bulunamadı!");
        }
    }
}
