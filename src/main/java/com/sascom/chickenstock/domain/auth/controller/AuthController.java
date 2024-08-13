package com.sascom.chickenstock.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountDuplicateException;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotEnoughException;
import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.response.AccountInfoForLogin;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.service.MemberService;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AuthException;
import com.sascom.chickenstock.global.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final AccountService accountService;
    private final JwtProperties jwtProperties;
    private final String BASE_URI;

    @Value("${oauth.redirect-uri}")
    private String oauthRedirectUri;

    @Autowired
    public AuthController(AuthService authService, MemberService memberService, AccountService accountService, JwtProperties jwtProperties, @Value("${oauth.base-uri}") String baseUri) {
        this.authService = authService;
        this.memberService = memberService;
        this.accountService = accountService;
        this.jwtProperties = jwtProperties;
        BASE_URI = baseUri;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RequestSignupMember requestSignupMember) {
        authService.signup(requestSignupMember);
//        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, BASE_URI).body("회원가입이 처리되었습니다!");
        return ResponseEntity.ok("회원가입이 처리되었습니다!");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginMember> login(@RequestBody RequestLoginMember requestLoginMember, HttpServletResponse response) {
        TokenDto tokenDto = authService.login(requestLoginMember);
        Member member = memberService.findByEmail(requestLoginMember.email());

        AccountInfoForLogin accountInfoForLogin = accountService.getInfoForLogin(member.getId());
        ResponseLoginMember responseLoginMember = new ResponseLoginMember(member, accountInfoForLogin);


        ResponseCookie accessTokenCookie = ResponseCookie.from(jwtProperties.accessToken().cookieName(), tokenDto.accessToken())
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtProperties.refreshToken().cookieName(), tokenDto.refreshToken())
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

//        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, BASE_URI).body(responseLoginMember);
        return ResponseEntity.ok(responseLoginMember);
    }

    @GetMapping("/login/{socialId}")
    public void socialAuth(@PathVariable(name = "socialId") String socialId, HttpServletResponse response) {

        String redirectUri = oauthRedirectUri + socialId;

        try {
            response.sendRedirect(redirectUri);
        } catch (IOException e) {
            throw AuthException.of(AuthErrorCode.OAUTH_REDIRECT_FAIL);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie accesstokenCookie = WebUtils.getCookie(request, jwtProperties.accessToken().cookieName());
        Cookie refreshtokenCookie = WebUtils.getCookie(request, jwtProperties.refreshToken().cookieName());

        if (accesstokenCookie == null || refreshtokenCookie == null) {
            return ResponseEntity.badRequest().build();
        }

        TokenDto reissuedToken = authService.reissue(accesstokenCookie.getValue(), refreshtokenCookie.getValue());

        response.addHeader(HttpHeaders.SET_COOKIE, reissuedToken.accessToken());
        response.addHeader(HttpHeaders.SET_COOKIE, reissuedToken.refreshToken());

        return ResponseEntity.ok("재발급 완료");
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Object> checkNickname(@PathVariable(name = "nickname") String nickname) {
        String validNickname = authService.isAvailableNickname(nickname);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("nickname", validNickname);
        return ResponseEntity.accepted().body(responseMap);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> checkEmail(@PathVariable(name = "email") String email) {
        String validEmail = authService.isAvailableEmail(email);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("email", validEmail);
        return ResponseEntity.accepted().body(responseMap);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie accesstokenCookie = WebUtils.getCookie(request, jwtProperties.accessToken().cookieName());
        Cookie refreshtokenCookie = WebUtils.getCookie(request, jwtProperties.refreshToken().cookieName());
        return ResponseEntity.ok(authService.logout(accesstokenCookie.getValue(), refreshtokenCookie.getValue()));
    }
}
