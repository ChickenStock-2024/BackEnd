package com.sascom.chickenstock.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;

//@Getter
//public class ChangeInfoRequest {
//    private String nickname;
//    private String oldPassword;
//    private String newPassword;
//    private String newPasswordCheck;
//}

public record ChangeInfoRequest(
        String nickname,
        String oldPassword,
        String newPassword,
        String newPasswordCheck
) {}