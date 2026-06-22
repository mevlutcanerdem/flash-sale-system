package com.flashsale.wallet_service.dto;

// neden adına request değil event dedik
// çünkü sipariş ver isteği (request ) order service de kaldı
// artık sipariş verildi bitti bu yaşanmış bil olay (event)
public record OrderEvent(String userId,String productId,Double amount) {
}
