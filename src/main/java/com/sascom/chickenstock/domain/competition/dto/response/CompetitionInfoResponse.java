package com.sascom.chickenstock.domain.competition.dto.response;

import java.time.LocalDateTime;

public record CompetitionInfoResponse(
        boolean ingCompetition,
        Long competitionId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt
) {

    public static CompetitionInfoResponse createInactiveCompetitionResponse() {
        return new CompetitionInfoResponse(false, null, null, null, null);
    }
}
