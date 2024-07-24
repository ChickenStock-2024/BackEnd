package com.sascom.chickenstock.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResponse {
    private Long memberId;
    private String nickname;
    private Integer rating;
    private Integer point;

    @Builder
    public MemberInfoResponse(Long memberId, String nickname, Integer rating, Integer point) {}
}
