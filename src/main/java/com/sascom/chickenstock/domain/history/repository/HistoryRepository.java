package com.sascom.chickenstock.domain.history.repository;

import com.sascom.chickenstock.domain.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
