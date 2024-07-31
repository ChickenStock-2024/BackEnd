package com.sascom.chickenstock.domain.rival.repository;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.rival.entity.Rival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.List;
import java.util.Optional;

public interface RivalRepository extends JpaRepository<Rival, Long> {
    List<Rival> findByMemberId(Long memberId);
}
