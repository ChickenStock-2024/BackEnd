package com.sascom.chickenstock.domain.competition.dto.request;

public record CompetitionParticipationRequest (
    Long memberId,
    Long competitionId
) { }
