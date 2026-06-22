package com.flashsale.order_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockService {
        private final StringRedisTemplate redisTemplate;

    public StockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // indirim başlamadan önce redis e stock yüklemek içim (ör : iphone 17 -> 100 )
    public void initializeStock(String productId, int quantity){
        redisTemplate.opsForValue().set("stock:" + productId , String.valueOf(quantity));
        System.out.println("Redis stokları yüklendi.Ürün : " + productId + "Adet: " + quantity);

    }
    // Işık hızında stok düşme metodu
    public boolean decreaseStock(String productId){
        String key = "stock:" + productId;

        // redisin decr metodu atomiktir aynı anda 10 bin kişi yüklense bile stok adedi - ye düşmez
        Long remainingStock = redisTemplate.opsForValue().decrement(key);


        if (remainingStock == null || remainingStock < 0 ){

            // eğer stok bitmişse veya ürün yoksa redis i sıfıra eşitliyoruz ki eskiye gitmesin

            redisTemplate.opsForValue().set(key,"0");
            System.out.println("Stok bitti!Ürün:  " + productId);
            return false;
        }
        System.out.println("Stoktan 1 adet düşürüldü.Kalan redis stoğu "  + remainingStock);
        return true;
    }

}
