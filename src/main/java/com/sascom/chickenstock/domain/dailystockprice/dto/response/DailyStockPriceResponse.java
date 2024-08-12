package com.sascom.chickenstock.domain.dailystockprice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
public record DailyStockPriceResponse (
        LocalDate dateTime,
        Long openingPrice, // 시가
        Long closingPrice, // 종가
        Long highPrice, // 고가
        Long lowPrice, // 저가
        Long volume // 거래량
) {
}
