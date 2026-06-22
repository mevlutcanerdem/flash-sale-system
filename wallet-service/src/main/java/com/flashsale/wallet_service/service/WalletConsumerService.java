package com.flashsale.wallet_service.service;


import com.flashsale.wallet_service.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service // bu sınıfı projeye dahil et ve sen yönet
public class WalletConsumerService {

    private final WalletService walletService;

    public WalletConsumerService(WalletService walletService) {
        this.walletService = walletService;
    }
    //  @KafkaListener : spring boot a diyoruz ki
    // bu metodun kulağına bir kulaklık tak ve kafkadaki order-events radyosunu dinle
    // eğer o radyoda bir mesaj çalarsa , o mesajı al , orderevent kalıbına dönüştür ve bu metoda ver

        @KafkaListener(topics = "order-events",groupId = "wallet-group-v3")
    public void consumeOrder(String rawMessage){
            System.out.println("---------------------");
            System.out.println("KAFKADAN YENİ MESAJ YAKALANDI");
            System.out.println("GELEN VERİ : " + rawMessage);
            System.out.println("----------------------");

            try {
                // şimdilik gelen string i manuel parçalıyoruz (jackson kütüp ekleneck)4
                // gelen veri formatı {"userId" : "usr_9981" ,"amount" : "1500.0"}"

                String userId = rawMessage.split("\"userId\":\"")[1].split("\"")[0];
                String amountStr = rawMessage.split("\"amount\":")[1].split("}")[0];
                Double amount = Double.parseDouble(amountStr);


                walletService.deductBalance(userId,amount);

            }
            catch (Exception e){
                System.out.println("Para düşme işlemi başarısız oldu : "  + e.getMessage());

            }
            System.out.println("----------------");
        }
}
