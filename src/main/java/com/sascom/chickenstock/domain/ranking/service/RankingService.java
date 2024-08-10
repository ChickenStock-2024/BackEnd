package com.sascom.chickenstock.domain.ranking.service;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.ranking.dto.response.RankingListResponse;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import com.sascom.chickenstock.domain.rival.repository.RivalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final MemberRepository memberRepository;
    private final RivalRepository rivalRepository;
    private List<MemberRankingDto> cachedTotalRankingList;

    @Autowired
    public RankingService(MemberRepository memberRepository, RivalRepository rivalRepository) {
        this.memberRepository = memberRepository;
        this.rivalRepository = rivalRepository;
    }

    @PostConstruct
    public void init() {
        cachedTotalRankingList = memberRepository.findAllMemberInfos();
        for(int i = 0; i < cachedTotalRankingList.size(); i++) {
            if(cachedTotalRankingList.get(i).getCompetitionCount() > 0) {
                cachedTotalRankingList.get(i).addRating(RatingCalculatorV1.INITIAL_RATING);
            }
        }
        cachedTotalRankingList.sort((lhs, rhs) -> -Integer.compare(lhs.getRating(), rhs.getRating()));
        for(int i = 0, j = 0; i < cachedTotalRankingList.size(); i = j) {
            while(j < cachedTotalRankingList.size() &&
                    cachedTotalRankingList.get(i).getRating() == cachedTotalRankingList.get(j).getRating()) {
                cachedTotalRankingList.get(j++).updateRanking(i + 1);
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

    public RankingListResponse lookUpPaginationTotalRanking(int offset) {
        if(cachedTotalRankingList == null) {
            // TODO: change into ranking exception
            throw new IllegalStateException("server logic error");
        }

        // later, pageSize can be parameter passed from request.
        int pageSize = 10;

        // invalid offset error
        if(cachedTotalRankingList.isEmpty()) {
            if(offset != 1){
                // TODO: change into ranking exception
                throw new IllegalArgumentException("not found");
            }
        }
        else {
            if(offset <= 0 || (long)pageSize * (offset - 1) > (long) cachedTotalRankingList.size()) {
                // TODO: change into ranking exception
                throw new IllegalArgumentException("not found");
            }
        }

        return getRankingListResponse(cachedTotalRankingList, pageSize, offset);
    }

    public RankingListResponse lookUpPaginationRivalRanking(int offset) {
        // TODO: context holder에서 member 조회해오는 걸로 수정
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Authorization error"));

        Set<Long> rivals = rivalRepository.findByMemberId(member.getId())
                .stream()
                .map(rival -> rival.getEnemy().getId())
                .collect(Collectors.toUnmodifiableSet());
        List<MemberRankingDto> rivalRankingList = cachedTotalRankingList.stream()
                .filter(memberRankingDto ->
                        rivals.contains(memberRankingDto.getMemberId())
                                || memberRankingDto.getMemberId().equals(member.getId())
                )
                .map(memberRankingDto -> MemberRankingDto.builder()
                        .memberId(memberRankingDto.getMemberId())
                        .nickname(memberRankingDto.getNickname())
                        .imgUrl(memberRankingDto.getImgUrl())
                        .profit(memberRankingDto.getProfit())
                        .rating(memberRankingDto.getRating())
                        .competitionCount(memberRankingDto.getCompetitionCount())
                        .ranking(memberRankingDto.getRanking())
                        .build()
                )
                .toList();
        if(rivalRankingList.isEmpty()) {
            // TODO: change into ranking exception
            throw new IllegalStateException("server logic error");
        }

        int pageSize = 10;
        if(offset <= 0 || (long)pageSize * (offset - 1) > (long) rivalRankingList.size()) {
            // TODO: change into ranking exception
            throw new IllegalArgumentException("not found");
        }
        return getRankingListResponse(rivalRankingList, pageSize, offset);
    }

    private RankingListResponse getRankingListResponse(List<MemberRankingDto> RankingList, int pageSize, int offset) {
        return RankingListResponse.builder()
                .memberList(
                        RankingList
                                .subList(
                                        pageSize * (offset - 1),
                                        Math.min(RankingList.size(), pageSize * offset)
                                )
                                .stream()
                                .map(memberRankingDto -> MemberRankingDto.builder()
                                        .memberId(memberRankingDto.getMemberId())
                                        .nickname(memberRankingDto.getNickname())
                                        .imgUrl(memberRankingDto.getImgUrl())
                                        .profit(memberRankingDto.getProfit())
                                        .rating(memberRankingDto.getRating())
                                        .competitionCount(memberRankingDto.getCompetitionCount())
                                        .ranking(memberRankingDto.getRanking())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}
