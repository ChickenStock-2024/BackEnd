package com.sascom.chickenstock.domain.trade.dto;

public record RealStockTradeDto(
        String code,
        Integer currentPrice,
        Integer transactionVolume,
        TradeType tradeType
) {
}
