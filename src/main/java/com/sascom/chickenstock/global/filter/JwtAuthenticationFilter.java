package com.sascom.chickenstock.global.filter;

import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AccessTokenExpireException;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;
import com.sascom.chickenstock.global.error.exception.TokenNotFoundException;
import com.sascom.chickenstock.global.jwt.JwtProperties;
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
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtResolver jwtResolver;
    private final JwtProperties jwtProperties;
    private final RedisService redisService;

    @Autowired
    public JwtAuthenticationFilter(JwtResolver jwtResolver, JwtProperties jwtProperties, RedisService redisService) {
        this.jwtResolver = jwtResolver;
        this.jwtProperties = jwtProperties;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            Cookie accesTokenCookie = WebUtils.getCookie(request, jwtProperties.accessToken().cookieName());
            if (accesTokenCookie == null) {
                ChickenStockException e = TokenNotFoundException.of(AuthErrorCode.TOKEN_NOT_FOUND);
                request.setAttribute("exception", e);
                throw e;
            }

            String accessToken = accesTokenCookie.getValue();

            if (!StringUtils.hasText(accessToken)) {
                ChickenStockException e = TokenNotFoundException.of(AuthErrorCode.TOKEN_NOT_FOUND);
                request.setAttribute("exception", e);
                throw e;
            }

            if (!jwtResolver.isValidToken(accessToken)) {
                ChickenStockException e = AccessTokenExpireException.of(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
                request.setAttribute("exception", e);
                throw e;
            }

            // 블랙리스트에 올라간 Access Token 검증
            Optional<String> blacklistToken = redisService.getValues(accessToken);
            if(blacklistToken.isEmpty()) {
                Authentication authentication = jwtResolver.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (TokenNotFoundException | AccessTokenExpireException e) {
            log.error("exception class:{}, message: {}", e.getClass(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}