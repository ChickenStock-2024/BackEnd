package com.sascom.chickenstock.domain.fcmtoken.controller;

import com.sascom.chickenstock.domain.fcmtoken.dto.request.FcmTokenRequest;
import com.sascom.chickenstock.domain.fcmtoken.dto.response.FcmTokenResponse;
import com.sascom.chickenstock.domain.fcmtoken.entity.FcmToken;
import com.sascom.chickenstock.domain.fcmtoken.repository.FcmTokenRepository;
import com.sascom.chickenstock.domain.fcmtoken.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<FcmTokenResponse> saveFcmToken(@RequestBody FcmTokenRequest request) {
        FcmTokenResponse fcmTokenResponse = fcmTokenService.storeFcmToken(request.memberId(), request.fcmToken());

        return ResponseEntity.ok(fcmTokenResponse);
    }
}
