package com.flashsale.wallet_service.service;

import com.flashsale.wallet_service.entity.Wallet;
import com.flashsale.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    @Transactional
    public void deductBalance(String userId,Double amount){
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cüzdan bulunamadı! kullanıcı: " + userId));
        if (wallet.getBalance() < amount){
            System.out.println( "Hata.Yetersiz Bakiye! Kullanıcı : " + userId);
            throw new RuntimeException("Yetersiz bakiye!");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
        System.out.println("Bakiye başarılı bir şekilde düşürüldü.Yeni bakiye : " + wallet.getBalance());
    }
}
