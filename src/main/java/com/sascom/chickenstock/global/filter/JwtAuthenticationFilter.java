package com.sascom.chickenstock.global.filter;

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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtResolver jwtResolver;

    @Autowired
    public JwtAuthenticationFilter(JwtResolver jwtResolver) {
        this.jwtResolver = jwtResolver;
    }

    // TODO Jwt 인증 필터 구현
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();

            for (Cookie cookie : cookies) {
                System.out.println(cookie.getValue());
            }

            if (cookies.length < 2) {
                filterChain.doFilter(request, response);
            } else {
                Authentication authentication = jwtResolver.getAuthentication(cookies[1].getValue());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.info("jwt 인증 예외 발생");
            log.info("uri: {}", request.getRequestURI());
            response.sendError(1000);
        }
    }
}
