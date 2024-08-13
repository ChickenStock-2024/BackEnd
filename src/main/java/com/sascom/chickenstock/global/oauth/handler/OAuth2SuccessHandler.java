package com.sascom.chickenstock.global.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.member.service.MemberFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final String BASE_URI;
    private final MemberFacade memberFacade;
    private final ObjectMapper objectMapper;

    @Autowired
    public OAuth2SuccessHandler(
            @Value("${oauth.base-uri}") String baseUri,
            MemberFacade memberFacade,
            ObjectMapper objectMapper
    ) {
        this.BASE_URI = baseUri;
        this.memberFacade = memberFacade;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        ResponseLoginMember memberInfoForLogin = memberFacade.getLoginInfo(response, authentication);
        try(ServletOutputStream os = response.getOutputStream()) {
            String body = objectMapper.writeValueAsString(memberInfoForLogin);
            os.print(body);
            os.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}