package com.sascom.chickenstock.domain.member.repository;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findFirst10ByNicknameStartingWithOrderByNickname(String prefix);

    @Query(value = "SELECT " +
            "member.member_id AS memberId, " +
            "member.nickname AS nickname, " +
            "COALESCE(SUM(account.balance), 0) AS profit, " +
            "COALESCE(SUM(account.rating_change), 0) AS rating, " +
            "COUNT(account.account_id) AS competitionCount," +
            "RANK() OVER (ORDER BY SUM(account.rating_change) DESC) AS ranking " +
            "FROM member " +
            "JOIN account ON member.member_id = account.member_id " +
            "LIMIT :offset, 10",
            nativeQuery = true)
    List<MemberRankingDto> test(@Param("offset") int offset);

    @Query("SELECT new com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto(" +
            "m.id, " +
            "m.nickname, " +
            "m.imgName, " +
            "SUM(a.balance), " +
            "SUM(a.ratingChange), " +
            "COUNT(a.id)) " +
            "FROM Member m " +
            "LEFT JOIN m.accounts a " +
            "GROUP BY m.id, m.nickname")
    List<MemberRankingDto> findAllMemberInfos();

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
