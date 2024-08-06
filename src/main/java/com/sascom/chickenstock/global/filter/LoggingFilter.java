package com.sascom.chickenstock.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Request 정보를 로깅
        logRequestDetails(request);

        // 응답을 가로채기 위해 래퍼를 사용
        CustomHttpServletResponseWrapper responseWrapper = new CustomHttpServletResponseWrapper(response);

        // 다음 필터 또는 서블릿 실행
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            // Response 정보를 로깅
            logResponseDetails(responseWrapper);
        }
    }

    private void logRequestDetails(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        // Request URL과 메소드
        sb.append("Request URL: ").append(request.getRequestURL()).append("\n");
        sb.append("Request Method: ").append(request.getMethod()).append("\n");

        // Request Headers
        sb.append("Request Headers: ").append("\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            sb.append(headerName).append(": ").append(headerValue).append("\n");
        }

        // Request Parameters
        sb.append("Request Parameters: ").append("\n");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            sb.append(paramName).append(": ").append(paramValue).append("\n");
        }

        // 로깅
        logger.info(sb.toString());
    }

    private void logResponseDetails(CustomHttpServletResponseWrapper response) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Response 상태 코드
        sb.append("Response Status: ").append(response.getStatus()).append("\n");

        // Response Headers
        sb.append("Response Headers: ").append("\n");
        for (String headerName : response.getHeaderNames()) {
            String headerValue = response.getHeader(headerName);
            sb.append(headerName).append(": ").append(headerValue).append("\n");
        }

        // Response Body
        String responseBody = response.getCaptureAsString();
        sb.append("Response Body: ").append(responseBody).append("\n");

        // 로깅
        logger.info(sb.toString());
    }

    private class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter charArrayWriter = new CharArrayWriter();
        private PrintWriter writer = new PrintWriter(charArrayWriter);

        public CustomHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        public String getCaptureAsString() throws IOException {
            writer.flush();
            return charArrayWriter.toString();
        }
    }
}