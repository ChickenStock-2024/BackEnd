package com.sascom.chickenstock.domain.dailystockprice.repository;

import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyStockPriceRepository extends JpaRepository<DailyStockPrice, Long> {
    List<DailyStockPrice> findDailyStockPriceByCompanyId(Long companyId);
}
