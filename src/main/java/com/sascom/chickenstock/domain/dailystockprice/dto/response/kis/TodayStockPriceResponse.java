package com.sascom.chickenstock.domain.dailystockprice.dto.response.kis;

import java.util.List;

public record TodayStockPriceResponse (
        TodayStockInfo output1,
        List<TodayStock> output2,
        String rt_cd,
        String msg_cd,
        String msg1
) {
}
