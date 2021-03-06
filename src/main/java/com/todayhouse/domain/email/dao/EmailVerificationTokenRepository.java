package com.todayhouse.domain.email.dao;

import com.todayhouse.domain.email.domain.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByEmail(String Email);

    Optional<EmailVerificationToken> findByEmailAndTokenAndExpiredAtAfterAndExpired
            (String email, String token, LocalDateTime now, boolean expired);

    Optional<EmailVerificationToken> findByEmailAndExpired(String email, boolean expired);
}
