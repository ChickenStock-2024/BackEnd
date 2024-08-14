package com.sascom.chickenstock.domain.account.dto.response;


import com.sascom.chickenstock.domain.trade.dto.TradeType;

public record UnexcutedStockInfo(
        Long companyId,
        Integer price,
        Integer volume,
        TradeType tradeType
) {
}
