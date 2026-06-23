package com.flashsale.wallet_service.service;


import com.flashsale.wallet_service.dto.OrderEvent;
import com.flashsale.wallet_service.dto.PaymentFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // bu sınıfı projeye dahil et ve sen yönet
public class WalletConsumerService {

    private final WalletService walletService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public WalletConsumerService(WalletService walletService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.walletService = walletService;
        this.kafkaTemplate = kafkaTemplate;
    }
    //  @KafkaListener : spring boot a diyoruz ki
    // bu metodun kulağına bir kulaklık tak ve kafkadaki order-events radyosunu dinle
    // eğer o radyoda bir mesaj çalarsa , o mesajı al , orderevent kalıbına dönüştür ve bu metoda ver


        // String rawMessage yerine doğrudan OrderEvent alınıyor DTO olarak
        @KafkaListener(topics = "order-events-v2",groupId = "wallet-group-v7")
    public void consumeOrder(OrderEvent orderEvent){
            System.out.println("---------------------");
            System.out.println("Yeni sipariş paketi alındı");
            System.out.println("----------------------");
            // record kullandığımız için verileri bu şekilde çekiyoruz
            System.out.println("Sipariş no : " + orderEvent.orderNumber());
            System.out.println("Kullanıcı : " + orderEvent.userId());
            System.out.println("Çekilecek tutar : " + orderEvent.amount());
            try {
              // string parçalamadan doğrudan objeden alıyoruz
                walletService.deductBalance(orderEvent.userId(),orderEvent.amount());
                System.out.println("Para düşme işlemi BAŞARILI!");
            }
            catch (Exception e){
                System.out.println("Para düşme işlemi başarısız oldu : "  + e.getMessage());

                // Bakiye yetersizse Sipariş servisine iptal bildirimini (Rollback) fırlatıyoruz!
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        orderEvent.orderNumber(),
                        orderEvent.userId(),
                        "Yetersiz Bakiye veya Cüzdan Hatası"
                );
                // payment-failed-events-v2 kanalına mesaj fırlatıyoruz ki Order Service bunu duysun!
                kafkaTemplate.send("payment-failed-events-v2", failedEvent);
                System.out.println("🔄 SAGA: Sipariş iptali için Kafka'ya fırlatıldı!");
            }

            System.out.println("----------------");
        }

}
