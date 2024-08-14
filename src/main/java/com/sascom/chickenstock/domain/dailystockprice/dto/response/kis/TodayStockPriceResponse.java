package com.sascom.chickenstock.domain.dailystockprice.dto.response.kis;

import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record TodayStockPriceResponse (
        TodayStockInfo output1,
        List<TodayStock> output2,
        String rt_cd,
        String msg_cd,
        String msg1
) {

    public DailyStockPrice toDailyStockPrice(Long companyId, String nowStr) {
        TodayStock todayStock = output2.get(0);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate lDate = LocalDate.parse(nowStr, format);

        return DailyStockPrice.builder()
                .companyId(companyId)
                .openingPrice(Long.parseLong(todayStock.stck_oprc()))
                .closingPrice(Long.parseLong(todayStock.stck_clpr()))
                .highPrice(Long.parseLong(todayStock.stck_hgpr()))
                .lowPrice(Long.parseLong(todayStock.stck_lwpr()))
                .volume(Long.parseLong(todayStock.acml_vol()))
                .dateTime(lDate)
                .build();
    }
}
