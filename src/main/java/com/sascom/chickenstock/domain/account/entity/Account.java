package com.sascom.chickenstock.domain.account.entity;


import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account")
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @NotNull
    @Column(name = "balance")
    private Long balance;

    @OneToMany(mappedBy = "account")
    private List<History> histories;

    @NotNull
    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "rating_change")
    private Integer ratingChange;

    public void updateRankingAndRatingChange(int ranking, int ratingChange) {
        if(ranking <= 0) {
            throw new IllegalArgumentException("ranking should be greater than 0");
        }
        this.ranking = ranking;
        this.ratingChange = ratingChange;
        return;
    }

    public Long updateBalance(long value) {
        return balance += value;
    }

    public Account(Member member, Competition competition) {
        this.member = member;
        this.competition = competition;
        this.balance = 50_000_000L;
        this.ranking = 0;
    }
}
