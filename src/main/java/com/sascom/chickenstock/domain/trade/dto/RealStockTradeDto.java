package com.sascom.chickenstock.domain.trade.dto;

public record RealStockTradeDto(
        Long companyId,
        Integer currentPrice,
        Integer transactionVolume,
        TradeType tradeType
) {
}
