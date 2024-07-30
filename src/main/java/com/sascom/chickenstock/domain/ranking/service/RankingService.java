package com.sascom.chickenstock.domain.ranking.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.ranking.dto.response.RankingListResponse;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final MemberRepository memberRepository;
    private List<MemberRankingDto> cachedRankingList;

    @Autowired
    public RankingService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostConstruct
    public void init() {
        cachedRankingList = memberRepository.findAllMemberInfos();
        for(int i = 0; i < cachedRankingList.size(); i++) {
            if(cachedRankingList.get(i).getCompetitionCount() > 0) {
                cachedRankingList.get(i).addRating(RatingCalculatorV1.INITIAL_RATING);
            }
        }
        cachedRankingList.sort((lhs, rhs) -> -Integer.compare(lhs.getRating(), rhs.getRating()));
        for(int i = 0, j = 0; i < cachedRankingList.size(); i = j) {
            while(j < cachedRankingList.size() &&
                    cachedRankingList.get(i).getRating() == cachedRankingList.get(j).getRating()) {
                cachedRankingList.get(j++).updateRanking(i + 1);
            }
        }
    }

//    @Transactional(readOnly = true)
//    public RankingListResponse lookUpPaginationRanking(int offset) {
//        List<MemberRankingDto> result = memberRepository.test((offset - 1) * 10);
//        return RankingListResponse.builder()
//                .memberList(result)
//                .build();
//    }

    public RankingListResponse lookUpPaginationRanking(int offset) {
        if(cachedRankingList == null) {
            // TODO: change into ranking exception
            throw new IllegalStateException("server logic error");
        }

        // later, pageSize can be parameter passed from request.
        int pageSize = 10;

        // invalid offset error
        if(cachedRankingList.isEmpty()) {
            if(offset != 1){
                throw new IllegalArgumentException("not found");
            }
        }
        else {
            if(offset < 0 || (long)pageSize * offset > (long)cachedRankingList.size()) {
                throw new IllegalArgumentException("not found");
            }
        }

        return RankingListResponse.builder()
                .memberList(
                        cachedRankingList.subList(pageSize * (offset - 1), Math.min(cachedRankingList.size(), pageSize * offset))
                                .stream()
                                .map((memberRankingDto) -> MemberRankingDto.builder()
                                        .memberId(memberRankingDto.getMemberId())
                                        .nickname(memberRankingDto.getNickname())
                                        .rating(memberRankingDto.getRating())
                                        .competitionCount(memberRankingDto.getCompetitionCount())
                                        .ranking(memberRankingDto.getRanking())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }
}
