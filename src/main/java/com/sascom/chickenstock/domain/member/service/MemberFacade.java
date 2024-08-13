package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.response.AccountInfoForLogin;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;
import com.sascom.chickenstock.global.jwt.JwtProperties;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberFacade {
    private final MemberService memberService;
    private final AccountService accountService;
//    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final boolean COOKIE_HTTP_ONLY = true;
    @Value("${chicken-stock.domain}")
    private String COOKIE_DOMAIN;
    private final String COOKIE_SAME_SITE = "NONE";
    private final boolean COOKIE_SECURE = true;

    @Autowired
    public MemberFacade(
            MemberService memberService, AccountService accountService,
            JwtProperties jwtProperties, JwtProvider jwtProvider,
            RedisService redisService
    ) {
        this.memberService = memberService;
        this.accountService = accountService;
        this.jwtProperties = jwtProperties;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    public ResponseLoginMember getLoginInfo(HttpServletResponse response, Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberInfoForLogin memberInfoForLogin = memberService.lookUpMemberInfoForLogin(memberId);
        AccountInfoForLogin accountInfoForLogin = accountService.getInfoForLogin(memberId);

        String accessToken = jwtProvider.createToken(authentication, jwtProvider.getAccessTokenExpirationDate());
        LocalDateTime refreshTokenExpirationDate = jwtProvider.getRefreshTokenExpirationDate();
        String refreshToken = jwtProvider.createToken(authentication, refreshTokenExpirationDate);

        setJwtTokensInCookie(response, accessToken, refreshToken);

        redisService.setValues(authentication.getName(), refreshToken, refreshTokenExpirationDate);

        return new ResponseLoginMember(memberInfoForLogin, accountInfoForLogin);
    }

    private void setJwtTokensInCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(jwtProperties.accessToken().cookieName(), accessToken)
                .httpOnly(COOKIE_HTTP_ONLY)
                .domain(COOKIE_DOMAIN)
                .sameSite(COOKIE_SAME_SITE)
                .secure(COOKIE_SECURE)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtProperties.refreshToken().cookieName(), refreshToken)
                .httpOnly(COOKIE_HTTP_ONLY)
                .domain(COOKIE_DOMAIN)
                .sameSite(COOKIE_SAME_SITE)
                .secure(COOKIE_SECURE)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.setStatus(HttpServletResponse.SC_OK);
    }
}