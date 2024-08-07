package com.sascom.chickenstock.global.oauth.handler;

import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.global.jwt.JwtProperties;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final String BASE_URI;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RedisService redisService;

    @Autowired
    public OAuth2SuccessHandler(@Value("${oauth.base-uri}") String baseUri, JwtProvider jwtProvider, JwtProperties jwtProperties, RedisService redisService) {
        this.BASE_URI = baseUri;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String accessToken = jwtProvider.createToken(authentication, jwtProvider.getAccessTokenExpirationDate());
        LocalDateTime refreshTokenExpirationDate = jwtProvider.getRefreshTokenExpirationDate();
        String refreshToken = jwtProvider.createToken(authentication, refreshTokenExpirationDate);

        ResponseCookie accessTokenCookie = ResponseCookie.from(jwtProperties.accessToken().cookieName(), accessToken)
                .httpOnly(true)
                .domain("localhost")
                .sameSite("None")
                .secure(true)
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtProperties.refreshToken().cookieName(), refreshToken)
                .httpOnly(true)
                .domain("localhost")
                .sameSite("None")
                .secure(true)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        redisService.setValues(authentication.getName(), refreshToken, refreshTokenExpirationDate);

//        response.addHeader(HttpHeaders.LOCATION, BASE_URI);
//        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
