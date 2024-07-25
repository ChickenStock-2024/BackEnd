package com.sascom.chickenstock.domain.ranking.dto.response;

import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import lombok.Getter;

import java.util.List;

@Getter
public record RankingListResponse(List<MemberRankingDto> memberList) { }
