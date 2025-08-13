package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumberEncrypted(String numberEncrypted);

    Page<Card> findAllByUserId(Pageable pageable, Long userId);

    Page<Card> findAllByUserIdAndStatus(Pageable pageable, Long userId, CardStatus status);
}
