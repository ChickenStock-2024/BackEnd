package com.sascom.chickenstock.domain.rating.entity;


import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rating")
public class Rating extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @Column(name = "sum")
    private Long sum;

    @Column(name = "latest_rating")
    private Integer latestRating;

    public Rating(Account account, Long sum, Integer latestRating) {
        this.account = account;
        this.sum = sum;
        this.latestRating = latestRating;
    }
}


