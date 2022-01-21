package com.todayhouse.domain.email.dao;

import com.todayhouse.domain.email.domain.EmailConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailConfirmTokenRepository extends JpaRepository<EmailConfirmToken,String> {
    Optional<EmailConfirmToken> findByEmail(String Email);
    Optional<EmailConfirmToken> findByEmailAndExpirationDateAfterAndExpired(String Email, LocalDateTime now, boolean expired);
}
