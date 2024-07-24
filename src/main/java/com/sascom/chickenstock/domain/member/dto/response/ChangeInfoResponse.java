package com.sascom.chickenstock.domain.member.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ChangeInfoResponse {
    private String nickname;

    public ChangeInfoResponse() {}

    public ChangeInfoResponse(String nickname) {
        this.nickname = nickname;
    }
}
