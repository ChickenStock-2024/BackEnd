package com.sascom.chickenstock.global.filter;

import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AccessTokenExpireException;
import com.sascom.chickenstock.global.error.exception.TokenNotFoundException;
import com.sascom.chickenstock.global.jwt.JwtProperties;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import com.sascom.chickenstock.global.jwt.JwtResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtResolver jwtResolver;
    private final JwtProvider jwtProvider;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProperties jwtProperties;

    @Autowired
    public JwtAuthenticationFilter(JwtResolver jwtResolver, JwtProvider jwtProvider, JwtProperties jwtProperties) {
        this.jwtResolver = jwtResolver;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            Cookie accesTokenCookie = WebUtils.getCookie(request, jwtProperties.accessToken().cookieName());
            if (accesTokenCookie == null) {
                throw TokenNotFoundException.of(AuthErrorCode.TOKEN_NOT_FOUND);
            }

            String accessToken = accesTokenCookie.getValue();

            if (!StringUtils.hasText(accessToken)) {
                throw TokenNotFoundException.of(AuthErrorCode.TOKEN_NOT_FOUND);
            }

            if (!jwtResolver.isValidToken(accessToken)) {
                throw AccessTokenExpireException.of(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
            }

            Authentication authentication = jwtResolver.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenNotFoundException | AccessTokenExpireException e) {
            log.error("exception class:{}, message: {}", e.getClass(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}