package com.sascom.chickenstock.domain.ranking.service;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.CompetitionResultDto;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.ranking.dto.response.RankingListResponse;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import com.sascom.chickenstock.domain.rival.repository.RivalRepository;
import com.sascom.chickenstock.global.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final MemberRepository memberRepository;
    private final RivalRepository rivalRepository;
    private List<MemberRankingDto> cachedTotalRankingList;

    private final int PAGE_SIZE = 10;

    @Autowired
    public RankingService(MemberRepository memberRepository, RivalRepository rivalRepository) {
        this.memberRepository = memberRepository;
        this.rivalRepository = rivalRepository;
    }

    @PostConstruct
    public void init() {
        cachedTotalRankingList = new CopyOnWriteArrayList<>(memberRepository.findAllMemberInfos());
        for (int i = 0; i < cachedTotalRankingList.size(); i++) {
            if (cachedTotalRankingList.get(i).getCompetitionCount() > 0) {
                cachedTotalRankingList.get(i).addRating(RatingCalculatorV1.INITIAL_RATING);
            }
        }
        updateRankingBoard();
    }

//    @Transactional(readOnly = true)
//    public RankingListResponse lookUpPaginationRanking(int offset) {
//        List<MemberRankingDto> result = memberRepository.test((offset - 1) * 10);
//        return RankingListResponse.builder()
//                .memberList(result)
//                .build();
//    }

    public RankingListResponse lookUpPaginationTotalRanking(int offset) {
        if (cachedTotalRankingList == null) {
            // TODO: change into ranking exception
            throw new IllegalStateException("server logic error");
        }

        // later, pageSize can be parameter passed from request.

        // invalid offset error
        if (cachedTotalRankingList.isEmpty()) {
            if (offset != 1) {
                // TODO: change into ranking exception
                throw new IllegalArgumentException("not found");
            }
        } else {
            if (offset <= 0 || (long) PAGE_SIZE * (offset - 1) > (long) cachedTotalRankingList.size()) {
                // TODO: change into ranking exception
                throw new IllegalArgumentException("not found");
            }
        }

        return getRankingListResponse(cachedTotalRankingList, offset);
    }

    public RankingListResponse lookUpPaginationRivalRanking(int offset) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

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
        if (rivalRankingList.isEmpty()) {
            // TODO: change into ranking exception
            throw new IllegalStateException("server logic error");
        }

        if (offset <= 0 || (long) PAGE_SIZE * (offset - 1) > (long) rivalRankingList.size()) {
            // TODO: change into ranking exception
            throw new IllegalArgumentException("not found");
        }
        return getRankingListResponse(rivalRankingList, offset);
    }

    public void joinNewMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        int nowRanking =
                cachedTotalRankingList.isEmpty() ?
                        1 :
                        (cachedTotalRankingList.get(cachedTotalRankingList.size() - 1).getRating() == 0 ?
                                cachedTotalRankingList.get(cachedTotalRankingList.size() - 1).getRanking() :
                                cachedTotalRankingList.get(cachedTotalRankingList.size() - 1).getRanking() + 1);
        MemberRankingDto memberRankingDto = MemberRankingDto.builder()
                .memberId(memberId)
                .nickname(member.getNickname())
                .imgUrl(member.getImgName())
                .profit(0L)
                .rating(0)
                .competitionCount(0)
                .ranking(nowRanking)
                .build();
        cachedTotalRankingList.add(memberRankingDto);
    }

    public MemberRankingDto getMyRanking() {
        return getRankingById(SecurityUtil.getCurrentMemberId());
    }

    public MemberRankingDto getRankingById(Long memberId) {
        return cachedTotalRankingList.stream()
                .filter(rankingDto -> rankingDto.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("cachedTotalRanking Error"));
    }

    public void updateRankingBoardByCompetitionResult(List<CompetitionResultDto> results) {
        for (CompetitionResultDto result : results) {
            MemberRankingDto memberRankingDto = cachedTotalRankingList
                    .stream()
                    .filter(rankingDto -> rankingDto.getMemberId().equals(result.memberId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("cachedTotalRakingList Error"));
            memberRankingDto.addRating(result.ratingChange());
            if (memberRankingDto.addCompetitionCount()) {
                memberRankingDto.addRating(RatingCalculatorV1.INITIAL_RATING);
            }
        }
        updateRankingBoard();
    }

    private void updateRankingBoard() {
        cachedTotalRankingList.sort((lhs, rhs) -> -Integer.compare(lhs.getRating(), rhs.getRating()));
        for (int i = 0, j = 0; i < cachedTotalRankingList.size(); i = j) {
            while (j < cachedTotalRankingList.size() &&
                    cachedTotalRankingList.get(i).getRating() == cachedTotalRankingList.get(j).getRating()) {
                cachedTotalRankingList.get(j++).updateRanking(i + 1);
            }
        }
    }

    private RankingListResponse getRankingListResponse(List<MemberRankingDto> rankingList, int offset) {
        return RankingListResponse.builder()
                .totalCount(rankingList.size())
                .myRanking(getMyRanking())
                .memberList(
                        rankingList
                                .subList(
                                        PAGE_SIZE * (offset - 1),
                                        Math.min(rankingList.size(), PAGE_SIZE * offset)
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
