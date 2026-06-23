package com.flashsale.wallet_service.dto;

// geriye (sipariş servisine) fırlatacağımız "ödeme başarısız " fişeği
public record PaymentFailedEvent(
        String orderNumber,
        String userId,
        String reason // neden başarısız oldu? (ör : "Yetersiz bakiye")

) {}
