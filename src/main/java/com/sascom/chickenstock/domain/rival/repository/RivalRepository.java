package com.sascom.chickenstock.domain.rival.repository;

import com.sascom.chickenstock.domain.rival.entity.Rival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RivalRepository extends JpaRepository<Rival, Long> {
    List<Rival> findByMemberId(Long memberId);
}
