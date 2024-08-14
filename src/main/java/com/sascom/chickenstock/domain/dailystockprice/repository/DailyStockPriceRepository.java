package com.sascom.chickenstock.domain.dailystockprice.repository;

import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStockPriceRepository extends JpaRepository<DailyStockPrice, Long> {
    List<DailyStockPrice> findDailyStockPriceByCompanyId(Long companyId);
    Optional<DailyStockPrice> findDailyStockPriceByDateTimeAndCompanyId(LocalDate localDate, Long companyId);
}
