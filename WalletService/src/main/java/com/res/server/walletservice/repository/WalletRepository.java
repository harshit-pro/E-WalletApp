package com.res.server.walletservice.repository;

import com.res.server.walletservice.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends CrudRepository<Wallet, Long> {
    Wallet findWalletByWalletId(String id);
    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance= w.balance + :amount  where w.walletId = :walletId")
    public void updateWallet(String walletId, Long amount);



}
