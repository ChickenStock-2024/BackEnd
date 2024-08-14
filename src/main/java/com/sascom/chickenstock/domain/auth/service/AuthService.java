package com.sascom.chickenstock.domain.auth.service;

import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountDuplicateException;
import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.service.RankingService;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AuthException;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import com.sascom.chickenstock.global.jwt.JwtResolver;
import com.sascom.chickenstock.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RankingService rankingService;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final JwtResolver jwtResolver;

    @Value("${image.default-img-name}")
    private String defaultImgName;

    @Transactional
    public void signup(RequestSignupMember requestSignupMember) {
        if (!requestSignupMember.password().equals(requestSignupMember.passwordCheck())) {
            throw new IllegalArgumentException(AuthErrorCode.SIGNUP_PASSWORD_MISMATCH.getMessage());
        }

        if (!isValidEmail(requestSignupMember.email())) {
            throw new IllegalArgumentException(AuthErrorCode.SIGNUP_INVALID_REQUEST.getMessage() + ": 이메일 형식이 올바르지 않습니다.");
        }

        String validNickname = isAvailableNickname(requestSignupMember.nickname());
        String validEmail = isAvailableEmail(requestSignupMember.email());

        Member member = new Member(
                validNickname,
                validEmail,
                passwordEncoder.encode(requestSignupMember.password()),
                defaultImgName);

        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw AccountDuplicateException.of(AccountErrorCode.DUPLICATED_VALUE);
        }
        rankingService.joinNewMember(member.getId());
    }
    // 이메일 정규식 검사
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public TokenDto login(RequestLoginMember requestLoginMember) {
        Authentication authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(requestLoginMember.email(), requestLoginMember.password());

        Authentication authResponse = authenticationManager.authenticate(authRequest);

        String accessToken = jwtProvider.createToken(authResponse, jwtProvider.getAccessTokenExpirationDate());
        LocalDateTime refreshTokenExpirationDate = jwtProvider.getRefreshTokenExpirationDate();
        String refreshToken = jwtProvider.createToken(authResponse, refreshTokenExpirationDate);

        redisService.setValues(authResponse.getName(), refreshToken, refreshTokenExpirationDate);

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto reissue(String accessToken, String refreshToken) {
        if (!jwtResolver.isValidToken(refreshToken)) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        Authentication authentication = jwtResolver.getAuthentication(accessToken);

        String storedRefreshToken = redisService.getValues(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (storedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        String newAccessToken = jwtProvider.createToken(authentication, jwtProvider.getAccessTokenExpirationDate());
        LocalDateTime refreshTokenExpirationDate = jwtProvider.getRefreshTokenExpirationDate();
        String newRefreshToken = jwtProvider.createToken(authentication, refreshTokenExpirationDate);

        redisService.setValues(authentication.getName(), refreshToken, refreshTokenExpirationDate);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    public String isAvailableNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw AuthException.of(AuthErrorCode.NICKNAME_CONFLICT);
        }

        return nickname;
    }

    public String isAvailableEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw AuthException.of(AuthErrorCode.EMAIL_INVALID);
        }

        if (memberRepository.existsByEmail(email)) {
            throw AuthException.of(AuthErrorCode.EMAIL_CONFLICT);
        }

        return email;
    }

    public String logout(String accessToken, String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtResolver.isValidToken(refreshToken)) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 memberId 추출
        Authentication authentication = jwtResolver.getAuthentication(accessToken);
        String storedRefreshToken = redisService.getValues(authentication.getName())
                .orElseThrow(() -> new RuntimeException("이미 로그아웃된 사용자입니다."));
        redisService.deleteValues(authentication.getName());

        // 3. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Date expiration = jwtResolver.getExpirationDate(accessToken);
        LocalDateTime expirationLocalDateTime = expiration.toInstant()
                .atZone(ZoneId.systemDefault())  // 시스템 기본 시간대 사용
                .toLocalDateTime();
        redisService.setValues(accessToken, "logout", expirationLocalDateTime);

        return "로그아웃 되었습니다.";
    }
}
