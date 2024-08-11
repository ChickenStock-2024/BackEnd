package com.sascom.chickenstock.domain.trade.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ProcessedOrderDto(
        Long accountId,
        Long requestHistoryId,
        Long companyId,
        Integer price,
        Integer volume,
        TradeType tradeType,
        OrderType orderType,
        MatchStatus matchStatus
) {
}
