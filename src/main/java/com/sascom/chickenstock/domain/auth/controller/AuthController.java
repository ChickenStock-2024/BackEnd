package com.sascom.chickenstock.domain.auth.controller;

import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.service.MemberService;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AuthException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;

    @Value("${oauth.redirect-uri}")
    private String oauthRedirectUri;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RequestSignupMember requestSignupMember) {
        authService.signup(requestSignupMember);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 처리되었습니다!");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginMember> login(@RequestBody RequestLoginMember requestLoginMember) {
        TokenDto tokenDto = authService.login(requestLoginMember);
        Member member = memberService.findByEmail(requestLoginMember.email());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-token", tokenDto.accessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseLoginMember(member));
    }

    @GetMapping("login/{socialId}")
    public void socialAuth(@PathVariable(name = "socialId") String socialId, HttpServletResponse response) {

        String redirectUri = oauthRedirectUri + socialId;

        try {
            response.sendRedirect(redirectUri);
        } catch (IOException e) {
            throw AuthException.of(AuthErrorCode.OAUTH_REDIRECT_FAIL);
        }
    }
}
