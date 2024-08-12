package com.sascom.chickenstock.domain.competition.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CompetitionListResponse(
        Long competitionId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer rank,
        Integer ratingChange,
        Long balance,
        Long accountId
) {
}