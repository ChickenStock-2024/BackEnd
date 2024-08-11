package com.sascom.chickenstock.domain.fcmtoken.repository;

import com.sascom.chickenstock.domain.fcmtoken.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByMemberId(Long memberId);
}
