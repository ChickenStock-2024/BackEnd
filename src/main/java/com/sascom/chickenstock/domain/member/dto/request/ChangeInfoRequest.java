package com.sascom.chickenstock.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeInfoRequest {
    private String nickname;
    private String oldPassword;
    private String newPassword;
    private String newPasswordCheck;
}