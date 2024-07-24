package com.sascom.chickenstock.domain.member.repository;

import com.sascom.chickenstock.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
