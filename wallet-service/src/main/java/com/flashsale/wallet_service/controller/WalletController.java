package com.flashsale.wallet_service.controller;


import com.flashsale.wallet_service.WalletServiceApplication;
import com.flashsale.wallet_service.entity.Wallet;
import com.flashsale.wallet_service.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/wallets")
public class WalletController {

    private final WalletService walletService;


    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deduct(@RequestParam String userId,@RequestParam Double amount){

        try {
            walletService.deductBalance(userId,amount);
            return ResponseEntity.ok("İşlem başarılı!Cüzdandan" + amount + " tl düşüldü");
        } catch (Exception e ){
            return ResponseEntity.badRequest().body("Cüzdan işlem hatası : " + e.getMessage());
        }

    }
}
