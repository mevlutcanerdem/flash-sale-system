package com.flashsale.order_service.model;

public enum OrderStatus {
    // sektörde durumlar asla string olarak
    // "bekliyor " , "iptal" diye el ile yazılmaz
    // typse-safety için enum kullanırız

    PENDING,    // Sipariş alındı, cüzdandan para düşmesi bekleniyor
    COMPLETED,  // Cüzdan onayı geldi , sipariş başarıyla tamamlandı
    FAILED,     // Cüzdanda bakiye yetersiz , sipariş iptal edildi
    REFUNDED    // İade edildi (gelecekteki senaryolar için )
}
