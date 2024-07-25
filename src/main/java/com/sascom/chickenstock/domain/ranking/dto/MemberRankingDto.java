package com.sascom.chickenstock.domain.ranking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public record MemberRankingDto(
        Long memberId,
        Integer rank,
        String nickname,
        Long sum,
        Integer rating,
        Integer competitionCount
) { }
