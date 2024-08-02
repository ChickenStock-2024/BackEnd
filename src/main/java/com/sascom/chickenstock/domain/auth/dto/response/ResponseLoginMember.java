package com.sascom.chickenstock.domain.auth.dto.response;

import com.sascom.chickenstock.domain.member.entity.Member;

public record ResponseLoginMember(
        Long memberId,
        String nickName
) {
    public ResponseLoginMember(Member member){
        this(member.getId(),member.getNickname());
    }
}
