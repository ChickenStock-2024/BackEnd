package com.sascom.chickenstock.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record MemberInfoResponse(
        Long memberId,
        String nickname,
        Integer rating,
        Integer point
) { }
