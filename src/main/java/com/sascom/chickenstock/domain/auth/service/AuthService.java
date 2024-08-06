package com.sascom.chickenstock.domain.auth.service;

import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.member.entity.Image;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.jwt.JwtProperties;
import com.sascom.chickenstock.global.jwt.JwtProvider;
import com.sascom.chickenstock.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public void signup(RequestSignupMember requestSignupMember) {
        if (!requestSignupMember.password().equals(requestSignupMember.password_check())) {
            throw new IllegalArgumentException(AuthErrorCode.SIGNUP_PASSWORD_MISMATCH.getMessage());
        }

        if (!isValidEmail(requestSignupMember.email())) {
            throw new IllegalArgumentException(AuthErrorCode.SIGNUP_INVALID_REQUEST.getMessage() + ": 이메일 형식이 올바르지 않습니다.");
        }
        Image defaultImage = new Image(null,"default_lmg.png", "C:\\Users\\SSAFY\\Image\\");
        Member member = new Member(
                requestSignupMember.nickname(),
                requestSignupMember.email(),
                passwordEncoder.encode(requestSignupMember.password()),
                defaultImage);
        memberRepository.save(member);
    }
    // 이메일 정규식 검사
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public TokenDto login(RequestLoginMember requestLoginMember) {
        Authentication authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(requestLoginMember.email(), requestLoginMember.password());

        Authentication authResponse = authenticationManager.authenticate(authRequest);

        String accessToken = jwtProvider.createToken(authResponse, jwtProvider.getAccessTokenExpirationDate());
        String refreshToken = jwtProvider.createToken(authResponse, jwtProvider.getAccessTokenExpirationDate());

        /**
         * TODO 한주형 체크
         * 이부분 어떻게 할건지?
         */
//        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//
//        // 4. RefreshToken 저장
////        RefreshToken refreshToken = RefreshToken.builder()
////                .key(authentication.getName())
////                .value(tokenDto.getRefreshToken())
////                .accessToken(tokenDto.getAccessToken())
////                .build();
////        refreshTokenRepository.save(refreshToken);
//
//        // 5. 토큰 발급
//        return tokenDto;

        return new TokenDto(null, null, jwtProperties.bearerType(), accessToken, refreshToken, null);
    }

//    @Transactional
//    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
//        // 1. Refresh Token 검증
//        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
//        }
//
//        // 2. Access Token 에서 Member ID 가져오기
//        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//
//        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
//        RefreshToken refreshToken = refreshTokenRepository.findByAccessToken(tokenRequestDto.getAccessToken())
//                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));
//
//        // 4. Refresh Token 일치하는지 검사
//        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
//            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
//        }
//        // 5. 새로운 토큰 생성
//        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//
//        // 6. 저장소 정보 업데이트
//        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken(), tokenDto.getAccessToken());
//        refreshTokenRepository.save(newRefreshToken);
//
//        // 토큰 발급
//        return tokenDto;
//    }
}
