package com.sascom.chickenstock.domain.member.dto;

public record MemberInfoForLogin(
        Long memberId,
        String nickname,
        boolean webNoti,
        boolean kakaotalkNoti
) {
}