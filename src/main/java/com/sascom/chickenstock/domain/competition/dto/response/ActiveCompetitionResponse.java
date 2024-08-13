package com.sascom.chickenstock.domain.competition.dto.response;

import java.time.LocalDateTime;

public record ActiveCompetitionResponse (
        boolean ingCompetition,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
