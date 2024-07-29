package com.sascom.chickenstock.domain.ranking.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.ranking.dto.response.RankingListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {
    // 분명 rival 랭킹과 전체 랭킹을 한 번에 처리할 수 있게 짤 수 있을 거 같다...
    // + 전체 유저가 적은데 저걸 매번 DB에서 복잡한 쿼리를 날려서 받아올게 아니라 여기에 List 형태로
    //   memoryRepository로 저장해두고 대회 끝날 때 마다 레이팅 계산하고 update 하면 되지 않나...?
    private final MemberRepository memberRepository;

    @Autowired
    public RankingService(MemberRepository memberRepository, AccountRepository accountRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public RankingListResponse lookUpPaginationRanking(int offset) {
        List<MemberRankingDto> result = memberRepository.test((offset - 1) * 10);
        return RankingListResponse.builder()
                .memberList(result)
                .build();
    }
}
