package com.sascom.chickenstock.domain.member.dto.response;

public record MemberInfoResponse(
        Long memberId,
        String nickname,
        Integer rating,
        Long balance,
        Integer ranking,
        Integer point,
        String imgUrl
) { }