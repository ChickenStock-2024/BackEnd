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
    public static final String ROLE = "role";

    @PostConstruct
    private void setSecretKey() {
        byte[] decode = Decoders.BASE64.decode(jwtProperties.secret());
        key = Keys.hmacShaKeyFor(decode);
    }

    public LocalDateTime getAccessTokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusSeconds(jwtProperties.accessToken().expireDuration().toSeconds());
    }

    public LocalDateTime getRefreshTokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusSeconds(jwtProperties.refreshToken().expireDuration().toSeconds());
    }

    /**
     * @param expirationTime > Use tokenProvider.getXXXTokenExpirationDate
     */
    public String createToken(Authentication authentication, LocalDateTime expirationTime) {
        Date expirationDate = TimeUtil.localDateTime2Date(expirationTime);

        String memberRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());
        return Jwts.builder()
                .issuer(ISSUER_NAME)
                .subject(authentication.getName())
                .claim(ROLE, memberRole)
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
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}