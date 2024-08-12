package com.sascom.chickenstock.domain.dailystockprice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_stock_price", indexes = {
        @Index(name = "idx_company_id_date_time", columnList = "companyId, dateTime")
})
public class DailyStockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_stock_price_id")
    private Long id;

    private Long companyId;

    private LocalDate dateTime;

    private Long openingPrice; // 시가
    private Long closingPrice; // 종가
    private Long highPrice; // 고가
    private Long lowPrice; // 저가
    private Long volume; // 거래량

}
