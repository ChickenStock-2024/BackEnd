package com.sascom.chickenstock.domain.auth.controller;

import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;

import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;
import com.sascom.chickenstock.domain.member.service.MemberFacade;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final MemberFacade memberFacade;
    private final AuthenticationManager authenticationManager;
    private final String BASE_URI;

    @Value("${oauth.redirect-uri}")
    private String oauthRedirectUri;

    @Autowired
    public AuthController(
            AuthService authService,
            MemberFacade memberFacade,
            JwtProperties jwtProperties,
            AuthenticationManager authenticationManager,
            @Value("${oauth.base-uri}") String baseUri
    ) {
        this.authService = authService;
        this.jwtProperties = jwtProperties;
        this.memberFacade = memberFacade;
        this.authenticationManager = authenticationManager;
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

        Authentication authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        requestLoginMember.email(), requestLoginMember.password());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        ResponseLoginMember responseLoginMember = memberFacade.getLoginInfo(response, authentication);
        return ResponseEntity.ok().body(responseLoginMember);
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
