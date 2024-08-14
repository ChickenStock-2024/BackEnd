package com.sascom.chickenstock.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.member.service.MemberFacade;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    public static final String KAKAO_LOGIN = "/kakaoLogin";
    private final String BASE_URI;
    private final MemberFacade memberFacade;

    @Autowired
    public OAuth2SuccessHandler(
            @Value("${oauth.base-uri}") String baseUri,
            MemberFacade memberFacade
    ) {
        this.BASE_URI = baseUri;
        this.memberFacade = memberFacade;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        memberFacade.getLoginInfo(response, authentication);
        response.sendRedirect(BASE_URI + KAKAO_LOGIN);
    }
}