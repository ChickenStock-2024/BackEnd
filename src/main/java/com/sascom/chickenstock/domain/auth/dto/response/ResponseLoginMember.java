package com.sascom.chickenstock.domain.auth.dto.response;

import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;

public record ResponseLoginMember(
        Long memberId,
        String nickName,
        boolean webNoti,
        boolean kakaotalkNoti,
        Integer rating,
        Long balance,
        Boolean isCompParticipant
) {
    public ResponseLoginMember(MemberInfoForLogin memberInfoForLogin, AccountInfoForLogin accountInfoForLogin){
        this(
                memberInfoForLogin.memberId(), memberInfoForLogin.nickName(),
                memberInfoForLogin.webNoti(), memberInfoForLogin.kakaotalkNoti(),
                accountInfoForLogin.rating(), accountInfoForLogin.balance(), accountInfoForLogin.isCompParticipant()

        );
    }
}