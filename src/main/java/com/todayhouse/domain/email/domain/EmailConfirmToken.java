package com.todayhouse.domain.email.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class EmailConfirmToken {
    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 3L;    //토큰 만료 시간

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column
    private LocalDateTime expirationDate;

    @Column
    private boolean expired;

    @Column
    private String email;

    @Column
    private String token;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // 인증 토큰 생성
    public static EmailConfirmToken createEmailToken(String email, String token) {
        EmailConfirmToken confirmationToken = new EmailConfirmToken();
        confirmationToken.expirationDate = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 3분후 만료
        confirmationToken.email = email;
        confirmationToken.token = token;
        confirmationToken.expired = false;
        return confirmationToken;
    }

    // 토큰 재전송
    public String updateToken(String token) {
        this.token = token;
        return this.id;
    }

    // 토큰 기한 만료
    public void useToken() {
        expired = true;
    }
}
