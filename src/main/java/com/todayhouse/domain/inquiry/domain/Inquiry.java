package com.todayhouse.domain.inquiry.domain;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(name = "is_buy")
    private boolean isBuy;

    @Column(name = "is_private")
    private boolean isPrivate;

    private String category;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public Inquiry(boolean isBuy, boolean isPrivate, String category, String content,
                   User user, Product product, Answer answer) {
        this.isBuy = isBuy;
        this.isPrivate = isPrivate;
        this.content = content;
        this.category = category;
        this.user = user;
        this.product = product;
        this.answer = answer;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setAnswer(Answer answer){
        this.answer = answer;
    }

    public void setProduct(Product product){
        this.product = product;
    }
}
