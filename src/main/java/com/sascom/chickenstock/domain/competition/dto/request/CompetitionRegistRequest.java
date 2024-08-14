package com.sascom.chickenstock.domain.competition.dto.request;

import lombok.NonNull;

public record CompetitionRegistRequest (
        @NonNull String title,
        @NonNull String startDate,
        @NonNull String endDate,
        @NonNull String adminKey
) {
}
