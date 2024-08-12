package com.sascom.chickenstock.domain.member.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword,
        String newPasswordCheck
) {}