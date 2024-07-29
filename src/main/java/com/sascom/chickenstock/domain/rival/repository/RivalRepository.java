package com.sascom.chickenstock.domain.rival.repository;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.rival.entity.Rival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RivalRepository extends JpaRepository<Rival, Long> {
    Optional<Rival> findByMemberAndEnemy(Member member, Member rival);
}
