package com.sascom.chickenstock.domain.account.dto.response;

import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;

import java.time.LocalDateTime;

public record HistoryInfo(
        String companyName,
        Integer price,
        Integer volume,
        HistoryStatus status,
        LocalDateTime createdAt
) {

}
