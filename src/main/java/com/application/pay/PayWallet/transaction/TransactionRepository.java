package com.application.pay.PayWallet.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Optional<Transaction> findByExternalId(String externalId);

    @Query("select tr from Transaction tr where fromUser =:uId")
    public List<Transaction> getStatementByUserIdAsSender(String uId);

    @Query("select tr from Transaction tr where toUser =:uId")
    public List<Transaction> getStatementByUserIdAsReceiver(String uId);

    @Query("select tr from Transaction tr where fromUser =:uId or toUser =:uId")
    public List<Transaction> getFullStatement(String uId);
}
