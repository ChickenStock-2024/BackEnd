package com.sascom.chickenstock.domain.fcmtoken.controller;

import com.sascom.chickenstock.domain.fcmtoken.dto.request.FcmTokenRequest;
import com.sascom.chickenstock.domain.fcmtoken.dto.response.FcmTokenResponse;
import com.sascom.chickenstock.domain.fcmtoken.entity.FcmToken;
import com.sascom.chickenstock.domain.fcmtoken.repository.FcmTokenRepository;
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

    private final FcmTokenRepository fcmTokenRepository;

    @PostMapping
    public ResponseEntity<FcmTokenResponse> saveFcmToken(@RequestBody FcmTokenRequest request) {
        // memberId로 FCM 토큰 엔티티를 조회
        Optional<FcmToken> existingFcmToken = fcmTokenRepository.findByMemberId(request.memberId());

        if (existingFcmToken.isPresent()) {
            // 존재하는 경우 토큰만 업데이트
            FcmToken fcmToken = existingFcmToken.get();
            fcmToken.setToken(request.fcmToken());
            // fcmTokenRepository.save(fcmToken);

        } else {
            // 존재하지 않는 경우 새로운 엔티티 생성 후 저장
            FcmToken newFcmToken = new FcmToken();
            newFcmToken.setMemberId(request.memberId());
            newFcmToken.setToken(request.fcmToken());
            fcmTokenRepository.save(newFcmToken);
        }

        return ResponseEntity.ok().body(
                FcmTokenResponse.builder()
                        .message("Success to Save FcmToken")
                        .fcmToken(request.fcmToken())
                        .memberId(request.memberId())
                        .build()
        );
    }
}
