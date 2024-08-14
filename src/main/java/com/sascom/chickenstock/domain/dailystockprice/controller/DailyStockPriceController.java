package com.sascom.chickenstock.domain.dailystockprice.controller;

import com.sascom.chickenstock.domain.dailystockprice.dto.response.DailyStockPriceResponse;
import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import com.sascom.chickenstock.domain.dailystockprice.service.DailyStockPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/daily-stock-price")
@RequiredArgsConstructor
public class DailyStockPriceController {

    private final DailyStockPriceService dailyStockPriceService;

    @GetMapping("/{company_id}")
    public ResponseEntity<List<DailyStockPriceResponse>> getDailyStockPrices(@PathVariable(name = "company_id") Long companyId) {
        List<DailyStockPriceResponse> dailyStockPriceResponseList = dailyStockPriceService.getDailyStockPrices(companyId);

        return ResponseEntity.ok().body(dailyStockPriceResponseList);
    }

    @GetMapping("/test")
    public ResponseEntity<List<DailyStockPrice>> test() {
        List<DailyStockPrice> response = dailyStockPriceService.automaticSaveDailyStockPrice();
        return ResponseEntity.ok().body(response);
    }

}
