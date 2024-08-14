package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.domain.auth.dto.response.AccountInfoForLogin;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import com.sascom.chickenstock.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberFacade {
    private final MemberService memberService;
    private final AccountService accountService;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    private final CookieUtil cookieUtil;

    @Autowired
    public MemberFacade(
            MemberService memberService,
            AccountService accountService,
            JwtProvider jwtProvider,
            RedisService redisService,
            CookieUtil cookieUtil) {
        this.memberService = memberService;
        this.accountService = accountService;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
        this.cookieUtil = cookieUtil;
    }

    public ResponseLoginMember getLoginInfo(HttpServletResponse response, Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName());

        MemberInfoForLogin memberInfoForLogin = memberService.lookUpMemberInfoForLogin(memberId);
        AccountInfoForLogin accountInfoForLogin = accountService.getInfoForLogin(memberId);

        String accessToken = jwtProvider.createToken(authentication, jwtProvider.getAccessTokenExpirationDate());
        LocalDateTime refreshTokenExpirationDate = jwtProvider.getRefreshTokenExpirationDate();
        String refreshToken = jwtProvider.createToken(authentication, refreshTokenExpirationDate);
        redisService.setValues(authentication.getName(), refreshToken, refreshTokenExpirationDate);

        cookieUtil.setAuthTokenCookie(accessToken, refreshToken, response);

        return new ResponseLoginMember(memberInfoForLogin, accountInfoForLogin);
    }
}