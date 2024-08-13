package com.sascom.chickenstock.domain.member.dto;

public record MemberInfoForLogin(
        Long memberId,
        String nickName,
        boolean webNoti,
        boolean kakaotalkNoti
) {
}