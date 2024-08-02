package com.sascom.chickenstock.global.jwt;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.global.util.TimeUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private SecretKey key;

    private static final String ISSUER_NAME = "chickenstock";
    public static final String NICKNAME = "nickname";

    @PostConstruct
    private void setSecretKey() {
        byte[] decode = Decoders.BASE64.decode(jwtProperties.getSecret());
        key = Keys.hmacShaKeyFor(decode);
    }

    public LocalDateTime getAccessTokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusSeconds(jwtProperties.getAccessToken().expireDuration().toSeconds());
    }

    public LocalDateTime getRefreshTokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusSeconds(jwtProperties.getRefreshToken().expireDuration().toSeconds());
    }

    @Deprecated
    public String createToken(Member member, LocalDateTime expirationTime) {
        Date expirationDate = TimeUtil.localDateTime2Date(expirationTime);

        return Jwts.builder()
                .issuer(ISSUER_NAME)
                .subject(member.getId().toString())
                .claim(NICKNAME, member.getNickname())
                .expiration(expirationDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * @param expirationTime > Use tokenProvider.getXXXTokenExpirationDate
     */
    public String createToken(Authentication authentication, LocalDateTime expirationTime) {
        Date expirationDate = TimeUtil.localDateTime2Date(expirationTime);

        return Jwts.builder()
                .issuer(ISSUER_NAME)
                .subject(authentication.getName())
                .expiration(expirationDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public boolean isValidToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            // TODO 만료 예외처리
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}