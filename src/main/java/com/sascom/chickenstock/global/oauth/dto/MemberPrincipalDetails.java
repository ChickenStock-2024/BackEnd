package com.sascom.chickenstock.global.oauth.dto;

import com.sascom.chickenstock.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record MemberPrincipalDetails(
        Member member,
        Map<String, Object> attributes,
        String attributeKey
) implements OAuth2User, ChickenStockUserDetails {

    @Override
    public String getUsername() {  // user 식별자 반환
        return member.getId().toString();
    }

    @Override
    public String getPassword() {  // user pw 반환
        return member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  // user 권한 반환
        return Collections.singletonList(new SimpleGrantedAuthority("member"));
    }

    @Override
    public String getName() {  // OAuth2에서 사용자 식별자 반환
        return attributes.get(attributeKey).toString();
    }

    @Override
    public Map<String, Object> getAttributes() {  // OAuth2로 받은 사용자 속성 반환
        return attributes;
    }
}
