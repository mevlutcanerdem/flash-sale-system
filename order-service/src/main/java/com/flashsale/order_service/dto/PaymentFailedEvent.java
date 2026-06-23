package com.flashsale.order_service.dto;

public record PaymentFailedEvent(
        String orderNumber,
        String userId,
        String reason
) {
}
