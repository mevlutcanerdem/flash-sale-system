package com.flashsale.order_service.service;

import com.flashsale.order_service.model.Order;
import com.flashsale.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
// Bu anatasyon, testlerimizde Mockito (dublör) kullanacağımızı belirtir
@ExtendWith(MockitoExtension.class)
public class OrderManagerServiceTest {

    // DUBLÖRLER (mocks)
    // gerçek redis, db ,kafka kullanmıyoruz.Onların yerine dublör atıyoruz

    @Mock
    private StockService stockService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String,Object> kafkaTemplate;


    // TEST EDİLECEK ASIL SINIF
    // dublörleri bu sınıfın içine enjekte et (injectMocks) diyoruz

    @InjectMocks
    private OrderManagerService orderManagerService;

    // 1.Senaryo: Stok var , Her şey yolunca (Happy Path)

    @Test
    void shouldProcessOrderSuccessfully_WhenStockIsAvailable(){
        // 1.Hazırlık (Arrange)
        // Dublör stock service e diyoruzki biri senden stock düşürmeni isterse true dön
        when(stockService.decreaseStock(anyString())).thenReturn(true);

        // 2.Aksiyon (act)
        // Test edeceğimiz asıl methodu çağırıyoruz
        String resultOrderNumber = orderManagerService.processOrder("macbook_pro","usr_123",500.0);

        // 3. Kontrol / İDDİA (Assert)
        // Sonuç null dönmemeli bize bir UUID dönmeli
        assertNotNull(resultOrderNumber,"Sipariş numarası null dönmemeli");

        // Dublör OrderRepository'nin "save" metodu tam 1 kez çağırıldımı kontrol ediyoruz (DB'e kayıt denendi mi )
        verify(orderRepository,times(1)).save(any(Order.class));
        // Dublör KafkaTemplate in "send" metodu tam 1 kez çağırıldı    mı kontrol et (Yani kafka ya mesaj gitti mi)

        verify(kafkaTemplate,times(1)).send(eq("order-events-v2"),any());

    }

    // 2.SENARYO : Stok yok sistem hata fırlatmalı  (Sad Path)
    @Test
    void shouldThrowException_WhenStockIsZero() {
        // 1.hazırlık (Arrange)
        //  Dublör stockservice e diyoruz ki biri senden stok düşürmeni isterse stok yok false dön

        when(stockService.decreaseStock(anyString())).thenReturn(false);

        // 2. Aksiyon ve kontrol (act & assert)
        // Metodu çağırdığımızda bir RunTimeException fırlatmasını bekliyoruz
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                    orderManagerService.processOrder("macbook_pro", "usr_123", 500.0);

        });

        // Fırlatılan hatanın mesajı doğru mu kontrol ediyoruz
        assertEquals("macbook_pro stokları tükendi",exception.getMessage());

        // ÇOK ÖNEMLİ : Stok olmadığı için DB'ye kayıt yapılmamış ve kafka'ya mesaj atılmamış olması gerek
        // Dublörlere soruyoruz: "Sizinle hiç iletişime geçildi mi " (GEÇİLMEMİŞ OLMASI GEREK)

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(kafkaTemplate);
    }
}
