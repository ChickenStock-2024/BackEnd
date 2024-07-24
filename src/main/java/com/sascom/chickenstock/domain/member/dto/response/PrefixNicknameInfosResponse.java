package com.sascom.chickenstock.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PrefixNicknameInfosResponse {
    private final List<MemberInfoResponse> memberList;

    @Builder
    public PrefixNicknameInfosResponse(List<MemberInfoResponse> memberList) {
        this.memberList = memberList;
    }
}
