package com.sascom.chickenstock.domain.account.dto.response;

import com.sascom.chickenstock.domain.history.entity.HistoryStatus;

import java.time.LocalDateTime;

public record HistoryInfo(
        String companyName,
        Integer price,
        Integer quantity,
        HistoryStatus status,
        LocalDateTime createdAt
) {
}
