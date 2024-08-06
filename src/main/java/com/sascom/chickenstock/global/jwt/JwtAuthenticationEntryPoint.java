package com.sascom.chickenstock.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.global.error.BaseExceptionHandler;
import com.sascom.chickenstock.global.error.dto.ErrorResponse;
import com.sascom.chickenstock.global.error.exception.ChickenStockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint extends BaseExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        Object requestException = request.getAttribute("exception");

        ChickenStockException exception;
        if (!(requestException instanceof ChickenStockException)) {
            // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
            log.info("JwtAuthenticationEntryPoint.commence");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        exception = (ChickenStockException) requestException;
        ResponseEntity<ErrorResponse> errorResponse = createErrorResponse(exception.getErrorCode());

        response.setStatus(exception.getErrorCode().getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}