package com.flashsale.wallet_service.dto;

// neden adına request değil event dedik
// çünkü sipariş ver isteği (request ) order service de kaldı
// artık sipariş verildi bitti bu yaşanmış bil olay (event)

// record yapısı bizim için tüm getter,setter ve constructor yapılarını arka planda yapar
public record OrderEvent(
        String orderNumber,
        String userId,
        Double amount
) {}
