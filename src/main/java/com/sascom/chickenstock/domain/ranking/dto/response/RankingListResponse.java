package com.sascom.chickenstock.domain.ranking.dto.response;

import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public record RankingListResponse(int totalCount, MemberRankingDto myRanking, List<MemberRankingDto> memberList) { }
