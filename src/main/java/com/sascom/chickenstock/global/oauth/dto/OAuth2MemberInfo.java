package com.sascom.chickenstock.global.oauth.dto;

import com.sascom.chickenstock.domain.member.entity.Member;

import java.util.Map;
import java.util.UUID;

public record OAuth2MemberInfo(
        String nickname, String email, String profilePath) {

    public static OAuth2MemberInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        };
    }

    private static OAuth2MemberInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> accountInfo = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profileInfo = (Map<String, Object>) accountInfo.get("profile");

        String email = (String)accountInfo.get("email");
        String nickname = (String) profileInfo.get("nickname");
        // TODO 기본 프로필 이미지 경로로 지정 or null?
        String profilePath = (String) profileInfo.get("profile_image_url");

        return new OAuth2MemberInfo(nickname, email, profilePath);
    }

    public Member toEntity() {
        // TODO 소셜계정 회원 랜덤 비밀번호 토의
        String randomPassword = UUID.randomUUID().toString();
        return new Member(nickname, email, randomPassword);
    }
}
