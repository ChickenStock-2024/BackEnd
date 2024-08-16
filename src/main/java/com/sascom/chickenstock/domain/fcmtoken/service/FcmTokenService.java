package com.sascom.chickenstock.domain.fcmtoken.service;

import com.sascom.chickenstock.domain.fcmtoken.dto.response.FcmTokenResponse;
import com.sascom.chickenstock.domain.fcmtoken.entity.FcmToken;
import com.sascom.chickenstock.domain.fcmtoken.repository.FcmTokenRepository;
import com.sascom.chickenstock.global.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    public String getFcmToken(Long memberId) {
        FcmToken fcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 없습니다."));

        return fcmToken.getToken();
    }

    public FcmTokenResponse storeFcmToken(Long memberId, String newToken) {

//        Long loginMemberId = SecurityUtil.getCurrentMemberId();
//        if (!loginMemberId.equals(memberId)) {
//            throw new IllegalArgumentException("로그인 멤버가 일치하지 않습니다.");
//        }

        Optional<FcmToken> candiFcmToken = fcmTokenRepository.findByMemberId(memberId);
        candiFcmToken.ifPresentOrElse(
                (oldToken) -> oldToken.updateToken(newToken),
                () -> saveFcmToken(memberId, newToken)
        );

        fcmTokenRepository.flush();

        return FcmTokenResponse.builder()
                .message("Success to Save FcmToken")
                .fcmToken(newToken)
                .memberId(memberId)
                .build();
    }

    public FcmToken saveFcmToken(Long memberId, String fcmToken) {
        FcmToken token = new FcmToken(memberId, fcmToken);
        return fcmTokenRepository.save(token);
    }
}
