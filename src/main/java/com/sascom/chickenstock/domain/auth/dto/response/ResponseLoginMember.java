package com.sascom.chickenstock.domain.auth.dto.response;

import com.sascom.chickenstock.domain.member.entity.Member;

public record ResponseLoginMember(
        Long memberId,
        String nickName,
        Integer rating,
        Long balance,
        Boolean isCompParticipant,
        boolean webNoti,
        boolean kakaotalkNoti
) {
    public ResponseLoginMember(Member member, AccountInfoForLogin accountInfoForLogin){
        this(
                member.getId(), member.getNickname(),
                accountInfoForLogin.rating(), accountInfoForLogin.balance(), accountInfoForLogin.isCompParticipant(),
                member.isWebNoti(), member.isKakaotalkNoti()
        );
    }
}
