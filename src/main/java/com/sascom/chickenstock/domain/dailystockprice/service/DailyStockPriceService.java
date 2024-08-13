package com.sascom.chickenstock.domain.dailystockprice.service;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.dailystockprice.dto.request.fis.AccessTokenReqeust;
import com.sascom.chickenstock.domain.dailystockprice.dto.response.DailyStockPriceResponse;
import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import com.sascom.chickenstock.domain.dailystockprice.repository.DailyStockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DailyStockPriceService {

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
                        .build())
                .toList();

        return dailyStockPriceResponseList;
    }

    // 매일 18시에 실행
    //@Scheduled(cron = "0 0 18 * * *")
    public ResponseEntity<?> automaticSaveDailyStockPrice() {
        LocalDate now = LocalDate.now();
        // 토큰 받기

        // POST https://openapivts.koreainvestment.com:29443/oauth2/tokenP
        /*
        {
            "grant_type": "client_credentials",
            "appkey": "PS74J7bOVo7WB25wGXUGOBMkWR7jBVSI0FmX",
            "appsecret":  "K7DFpzX4K4ddY8kqQHVArm3pYm/y92NXV100H0VDuhYaB6ITXAGKNIrGzIVVCLVWE03tuuQE1/vGnPuGgyQFCXJuhxe21mWnPR1jCjWLpsQSZQX4PEBGkFpxv4tu5ti966fC1DFeZPDti/xrr82in0tonHp1W50Xb8WW/gMcOIyy8PIRoOM="
        }
         */

        RestClient restClient = RestClient.create();
        ResponseEntity<Map> response = restClient.post()
                .uri("https://openapivts.koreainvestment.com:29443/oauth2/tokenP")
                .body(
                        AccessTokenReqeust.builder()
                                .grant_type("client_credentials")
                                .appkey("PS74J7bOVo7WB25wGXUGOBMkWR7jBVSI0FmX")
                                .appsecret("K7DFpzX4K4ddY8kqQHVArm3pYm/y92NXV100H0VDuhYaB6ITXAGKNIrGzIVVCLVWE03tuuQE1/vGnPuGgyQFCXJuhxe21mWnPR1jCjWLpsQSZQX4PEBGkFpxv4tu5ti966fC1DFeZPDti/xrr82in0tonHp1W50Xb8WW/gMcOIyy8PIRoOM=")
                                .build()
                )
                .retrieve()
                .toEntity(Map.class);

        String token = response.getBody().get("access_token").toString();

        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            Optional<DailyStockPrice> dailyStockPrice = dailyStockPriceRepository.findDailyStockPriceByDateTime(now);
            if (dailyStockPrice.isPresent()) {
                continue;
            }

            // 오늘 날짜로 일봉 데이터 가져와서 저장하기
            restClient = RestClient.builder()
                    .baseUrl("https://openapivts.koreainvestment.com:29443")
                    .defaultHeader("content-type", "application/json; charset=utf-8")
                    .defaultHeader("authorization", "application/json; charset=utf-8")
                    .defaultHeader("appkey", "application/json; charset=utf-8")
                    .defaultHeader("appsecret", "application/json; charset=utf-8")
                    .defaultHeader("tr_id", "application/json; charset=utf-8")
                    .defaultHeader("custtype", "application/json; charset=utf-8")
                    .build();

            String nowStr = now.format(DateTimeFormatter.ofPattern("YYYYMMdd"));

            ResponseEntity<?> response2 = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", "005930")
                            .queryParam("FID_INPUT_DATE_1", nowStr)
                            .queryParam("FID_INPUT_DATE_2", nowStr)
                            .queryParam("FID_PERIOD_DIV_CODE", "D")
                            .queryParam("FID_ORG_ADJ_PRC", "1")
                            .build())
                    .retrieve()
                    .toEntity(Map.class);
        }


    }
}
