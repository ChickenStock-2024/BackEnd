package com.sascom.chickenstock.domain.competition.dto.request;
import java.util.Date;

public record CompetitionRequest(
        String title,
        Date startAt,
        Date endAt
) { }
