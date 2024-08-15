package com.sascom.chickenstock.domain.dailystockprice.repository;

import com.sascom.chickenstock.domain.dailystockprice.entity.DailyStockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStockPriceRepository extends JpaRepository<DailyStockPrice, Long> {
    List<DailyStockPrice> findDailyStockPriceByCompanyId(Long companyId);
    Optional<DailyStockPrice> findDailyStockPriceByDateTimeAndCompanyId(LocalDate localDate, Long companyId);

    Optional<DailyStockPrice> findFirstByCompanyIdOrderByDateTimeDesc(Long companyId);
}
