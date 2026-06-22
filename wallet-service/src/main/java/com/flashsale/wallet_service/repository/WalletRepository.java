package com.flashsale.wallet_service.repository;

import com.flashsale.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findByUserId(String userId);
}
