package com.sascom.chickenstock.domain.member.repository;

import com.sascom.chickenstock.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findFirst10ByNicknameStartingWithOrderByNickname(String prefix);
}
