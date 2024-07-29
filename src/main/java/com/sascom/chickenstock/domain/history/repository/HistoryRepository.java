package com.sascom.chickenstock.domain.history.repository;

import com.sascom.chickenstock.domain.account.dto.response.HistoryInfo;
import com.sascom.chickenstock.domain.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {


    @Query("SELECT h FROM History h WHERE h.account.id = :accountId AND (h.status = '매수체결' OR h.status = '매도체결')")
    List<History> findExecutionContent(@Param("accountId") Long accountId);

}
