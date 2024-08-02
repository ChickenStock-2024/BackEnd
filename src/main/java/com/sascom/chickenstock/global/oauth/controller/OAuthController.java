package com.sascom.chickenstock.global.oauth.controller;

import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AuthException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth/login")
public class OAuthController {

    @Value("${oauth.redirect-uri}")
    private String oauthRedirectUri;

    @GetMapping("/{socialId}")
    public void socialAuth(
            @PathVariable(name = "socialId")
            String socialId,
            HttpServletResponse response
    ) {

        String redirectUri = oauthRedirectUri + socialId;

        try {
            response.sendRedirect(redirectUri);
        } catch (IOException e) {
            throw AuthException.of(AuthErrorCode.OAUTH_REDIRECT_FAIL);
        }
    }
}
