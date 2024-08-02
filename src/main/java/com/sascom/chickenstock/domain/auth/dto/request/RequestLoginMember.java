package com.sascom.chickenstock.domain.auth.dto.request;

import com.sascom.chickenstock.domain.member.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public record RequestLoginMember(
        String email,
        String password
){

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
