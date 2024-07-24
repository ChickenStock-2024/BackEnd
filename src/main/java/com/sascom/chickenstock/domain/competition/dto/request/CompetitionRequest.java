package com.sascom.chickenstock.domain.competition.dto.request;
import java.time.LocalDateTime;
import java.util.Date;

public record CompetitionRequest(
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt
) { }
