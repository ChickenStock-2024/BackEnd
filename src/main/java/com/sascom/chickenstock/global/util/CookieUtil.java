package com.sascom.chickenstock.global.util;

import com.sascom.chickenstock.global.jwt.JwtProperties;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CookieUtil {

    private final boolean COOKIE_HTTP_ONLY = true;
    private final JwtProperties jwtProperties;
    @Value("${chicken-stock.domain}")
    private String COOKIE_DOMAIN;
    @Value("${jwt.cookie-path}")
    private String COOKIE_PATH;
    private final String COOKIE_SAME_SITE = "NONE";
    private final boolean COOKIE_SECURE = true;

    public CookieUtil(JwtProperties jwtProperties, JwtProvider jwtProvider) {
        this.jwtProperties = jwtProperties;
    }

    public void setAuthTokenCookie(String accessToken, String refreshToken, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = createCookie(jwtProperties.accessToken().cookieName(), accessToken);
        ResponseCookie refreshTokenCookie = createCookie(jwtProperties.refreshToken().cookieName(), refreshToken);

        setCookie(accessTokenCookie, response);
        setCookie(refreshTokenCookie, response);
    }

    public void setCookie(HttpCookie cookie, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseCookie createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(COOKIE_HTTP_ONLY)
                .domain(COOKIE_DOMAIN)
                .path(COOKIE_PATH)
                .sameSite(COOKIE_SAME_SITE)
                .secure(COOKIE_SECURE)
                .build();

    }
}
