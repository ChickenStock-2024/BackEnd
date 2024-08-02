package com.sascom.chickenstock.domain.trade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public record ProcessedOrderDto(
        Long accountId,
        Long requestHistoryId,
        String companyName,
        Integer price,
        Integer volume,
        OrderType orderType,
        MatchStatus matchStatus
) {
}
