package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySenderCardUserId(Pageable pageable, Long userId);
    Page<Transaction> findByReceiverCardUserId(Pageable pageable, Long userId);
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderCard.user.id = :userId OR t.receiverCard.user.id = :userId")
    Page<Transaction> findAllByUserId(Pageable pageable, Long userId);
}
