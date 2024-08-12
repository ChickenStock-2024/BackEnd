package com.sascom.chickenstock.domain.dailystockprice.service;

import com.sascom.chickenstock.domain.dailystockprice.dto.response.DailyStockPriceResponse;
import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import com.sascom.chickenstock.domain.dailystockprice.repository.DailyStockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DailyStockPriceService {

    private final DailyStockPriceRepository dailyStockPriceRepository;

    public List<DailyStockPriceResponse> getDailyStockPrices(Long companyId) {
        List<DailyStockPriceResponse> dailyStockPriceResponseList = dailyStockPriceRepository.findDailyStockPriceByCompanyId(companyId)
                .stream()
                .sorted(Comparator.comparing(DailyStockPrice::getDateTime))// 날짜 오름차순으로 정렬
                .map(dailyStockPrice -> DailyStockPriceResponse.builder()
                        .dateTime(dailyStockPrice.getDateTime())
                        .openingPrice(dailyStockPrice.getOpeningPrice())
                        .closingPrice(dailyStockPrice.getClosingPrice())
                        .highPrice(dailyStockPrice.getHighPrice())
                        .lowPrice(dailyStockPrice.getLowPrice())
                        .build())
                .toList();

        return dailyStockPriceResponseList;
    }
}
