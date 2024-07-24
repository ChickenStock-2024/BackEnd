package com.sascom.chickenstock.domain.account.entity;


import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.rating.entity.Rating;
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
    @OneToOne(mappedBy = "account")
    private Rating rating;

    @NotNull
    @Column(name = "ranking")
    private Integer ranking;



    public Account(Member member, Competition competition) {
        this.member = member;
        this.competition = competition;
        this.balance = 50_000_000L;
    }
}
