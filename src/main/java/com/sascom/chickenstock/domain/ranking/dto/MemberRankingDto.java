package com.sascom.chickenstock.domain.ranking.dto;

public interface MemberRankingDto {
    Long getMemberId();
    Integer getRanking();
    String getNickname();
    Long getProfit();
    Integer getRating();
    Integer getCompetitionCount();
}