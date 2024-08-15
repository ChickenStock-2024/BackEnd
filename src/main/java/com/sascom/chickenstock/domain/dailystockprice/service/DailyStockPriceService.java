package com.sascom.chickenstock.domain.dailystockprice.service;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.dailystockprice.dto.request.fis.AccessTokenReqeust;
import com.sascom.chickenstock.domain.dailystockprice.dto.response.DailyStockPriceResponse;
import com.sascom.chickenstock.domain.dailystockprice.dto.response.kis.TodayStockPriceResponse;
import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import com.sascom.chickenstock.domain.dailystockprice.error.code.DailyStockPriceErrorCode;
import com.sascom.chickenstock.domain.dailystockprice.error.exception.KisTodayStockPriceException;
import com.sascom.chickenstock.domain.dailystockprice.error.exception.KisTokenException;
import com.sascom.chickenstock.domain.dailystockprice.repository.DailyStockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class DailyStockPriceService {

    @Value("${kis.app-key}")
    private String appKey;

    @Value("${kis.app-secret}")
    private String appSecret;

    private final DailyStockPriceRepository dailyStockPriceRepository;
    private final CompanyRepository companyRepository;

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
                        .volume(dailyStockPrice.getVolume())
                        .build())
                .toList();

        return dailyStockPriceResponseList;
    }

    public Long getLatestClosingPriceByCompanyId(Long companyId) {
        DailyStockPrice dailyStockPrice = dailyStockPriceRepository.findFirstByCompanyIdOrderByDateTimeDesc(companyId)
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));
        return dailyStockPrice.getClosingPrice();
    }

    // 매일 18시에 실행
    @Scheduled(cron = "0 0 18 * * *")
    public List<DailyStockPrice> automaticSaveDailyStockPrice() {
        LocalDate now = LocalDate.now();

        // 한국투자증권 API 토큰 받기 (access_token)
        RestClient restClient = RestClient.create();
        ResponseEntity<Map> kisTokenResponse = restClient.post()
                .uri("https://openapivts.koreainvestment.com:29443/oauth2/tokenP")
                .body(
                        AccessTokenReqeust.builder()
                                .grant_type("client_credentials")
                                .appkey(appKey)
                                .appsecret(appSecret)
                                .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw KisTokenException.of(DailyStockPriceErrorCode.CANNOT_GET_KIS_TOKEN);
                })
                .toEntity(Map.class);

        String token = kisTokenResponse.getBody().get("access_token").toString();

        // 각 회사 조회해서 당일 주가시세 정보 DB에 저장하기
        // 주말, 공휴일 -> 응답에 데이터가 없음 -> 저장 안함

        restClient = RestClient.builder()
                .baseUrl("https://openapivts.koreainvestment.com:29443")
                .defaultHeaders(
                        httpHeaders -> {
                            httpHeaders.set("content-type", "application/json; charset=utf-8");
                            httpHeaders.set("authorization", "Bearer " + token);
                            httpHeaders.set("appkey", appKey);
                            httpHeaders.set("appsecret", appSecret);
                            httpHeaders.set("tr_id", "FHKST03010100");
                            httpHeaders.set("custtype", "P");
                        })
                .build();

        String nowStr = now.format(DateTimeFormatter.ofPattern("YYYYMMdd"));

        List<DailyStockPrice> results = new ArrayList<>();

        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            Optional<DailyStockPrice> dailyStockPrice = dailyStockPriceRepository.findDailyStockPriceByDateTimeAndCompanyId(now, company.getId());
            if (dailyStockPrice.isPresent()) {
                continue;
            }

            // 오늘 날짜로 일봉 데이터 가져와서 저장하기

            // 한국투자증권 API 발사
            try {
                Thread.sleep(2000); //1초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResponseEntity<TodayStockPriceResponse> todayPriceResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", company.getCode())
                            .queryParam("FID_INPUT_DATE_1", nowStr)
                            .queryParam("FID_INPUT_DATE_2", nowStr)
                            .queryParam("FID_PERIOD_DIV_CODE", "D")
                            .queryParam("FID_ORG_ADJ_PRC", "1")
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw KisTodayStockPriceException.of(DailyStockPriceErrorCode.CANNOT_GET_TODAY_STOCK_PRICE);
                    })
                    .toEntity(TodayStockPriceResponse.class);

            // 저장
            dailyStockPriceRepository.save(
                    Objects.requireNonNull(todayPriceResponse.getBody()).toDailyStockPrice(company.getId(), nowStr)
            );
            results.add(todayPriceResponse.getBody().toDailyStockPrice(company.getId(), nowStr));
        }

        return results;

    }
}
