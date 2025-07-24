package com.res.server.transactionservice.dao;

import com.res.server.transactionservice.model.TransactionStatus;
import com.res.server.transactionservice.model.Transactions;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDao extends JpaRepository<Transactions, Long>, JpaSpecificationExecutor<Transactions>
        , CrudRepository<Transactions, Long>
 {

    @Transactional
    @Modifying
//    @Query("update transactions t set t.status = ?1 where t.externalTransactionId = ?2")
    @Query("UPDATE Transactions t SET t.status = ?1 WHERE t.externalTransactionId = ?2")
    public void updateTransactionStatus(TransactionStatus status, String externalTransactionId);

    Page<Transactions> findAll(Specification<Transactions> spec, Pageable pageable);
}
