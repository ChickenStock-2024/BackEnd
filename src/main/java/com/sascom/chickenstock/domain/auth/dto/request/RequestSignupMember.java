package com.sascom.chickenstock.domain.auth.dto.request;

public record RequestSignupMember(
        String email,
        String password,
        String passwordCheck,
        String nickname
) {
}
