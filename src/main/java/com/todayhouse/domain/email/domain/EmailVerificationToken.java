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
public class EmailVerificationToken {
    private static final long EMAIL_TOKEN_EXPIRATION_TIME_VALUE = 3L;    //토큰 만료 시간

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column
    private LocalDateTime expiredAt;

    @Column
    private String email;

    @Column
    private String token;

    @Column
    private boolean expired;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    // 인증 토큰 생성
    public static EmailVerificationToken createEmailToken(String email, String token) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.expiredAt = LocalDateTime.now().plusMinutes(EMAIL_TOKEN_EXPIRATION_TIME_VALUE); // 3분후 만료
        verificationToken.email = email;
        verificationToken.token = token;
        verificationToken.expired = false;
        return verificationToken;
    }

    // 토큰 재전송
    public String updateToken(String token) {
        this.token = token;
        return this.id;
    }

    // 토큰 만료
    public void expiredToken(){
        this.expired = true;
    }
}
