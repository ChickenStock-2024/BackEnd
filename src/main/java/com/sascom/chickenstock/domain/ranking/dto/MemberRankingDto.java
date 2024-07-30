package com.sascom.chickenstock.domain.ranking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRankingDto {
    private Long memberId;
    private String nickname;
    private long profit;
    private int rating;
    private int competitionCount;
    private int ranking;

    private MemberRankingDto() {}

    public MemberRankingDto(
            Long memberId,
            String nickname,
            Long profit,
            Long rating,
            Long competitionCount) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profit = profit == null? 0L : profit;
        this.rating = rating == null? 0 : rating.intValue();
        this.competitionCount = competitionCount == null? 0 : competitionCount.intValue();
    }

    @Builder
    public MemberRankingDto(
            Long memberId,
            String nickname,
            long profit,
            int rating,
            int competitionCount,
            int ranking
    ) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profit = profit;
        this.rating = rating;
        this.competitionCount = competitionCount;
        this.ranking = ranking;
    }

    public void addRating(int value) {
        rating += value;
    }

    public void updateRanking(int ranking) {
        this.ranking = ranking;
    }
}
