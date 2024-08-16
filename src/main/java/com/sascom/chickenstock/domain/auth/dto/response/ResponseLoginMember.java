package com.sascom.chickenstock.domain.auth.dto.response;

import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;

public record ResponseLoginMember(
        Long memberId,
        String nickname,
        Boolean webNoti,
        Boolean kakaotalkNoti,
        Integer rating,
        Long accountId,
        Long balance,
        Boolean isCompParticipant
) {
    public ResponseLoginMember(MemberInfoForLogin memberInfoForLogin, AccountInfoForLogin accountInfoForLogin){
        this(
                memberInfoForLogin.memberId(), memberInfoForLogin.nickname(),
                memberInfoForLogin.webNoti(), memberInfoForLogin.kakaotalkNoti(),
                accountInfoForLogin.rating(), accountInfoForLogin.accountId(),
                accountInfoForLogin.balance(), accountInfoForLogin.isCompParticipant()

        );
    }
}