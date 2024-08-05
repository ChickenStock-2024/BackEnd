package com.sascom.chickenstock.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public ResponseEntity<?> test(HttpServletRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        return ResponseEntity.ok(name);
    }
}
