package com.sascom.chickenstock.global.oauth.handler;

import com.sascom.chickenstock.domain.member.service.MemberFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    public static final String KAKAO_LOGIN_SUFFIX = "/kakaoLogin";
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
        response.sendRedirect(BASE_URI + KAKAO_LOGIN_SUFFIX);
    }
}