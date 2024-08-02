package com.sascom.chickenstock.domain.auth.controller;

import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;

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
        headers.add("Access-token", tokenDto.getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseLoginMember(member));
    }
}
