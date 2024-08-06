package com.sascom.chickenstock.global.jwt;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.global.oauth.dto.MemberPrincipalDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtResolver {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @Autowired
    public JwtResolver(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        byte[] decodeArr = Decoders.BASE64.decode(this.jwtProperties.secret());
        secretKey = Keys.hmacShaKeyFor(decodeArr);
    }

    public Long getMemberId(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getMemberNickname(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("nickname");
    }

    public Date getExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Long memberId = Long.valueOf(claims.getSubject());
        String nickname = (String) claims.get("nickname");

        Member member = Member.of(memberId, nickname);

        UserDetails memberPrincipal = new MemberPrincipalDetails(member, null, null);
        return new UsernamePasswordAuthenticationToken(memberPrincipal, null, memberPrincipal.getAuthorities());
    }

    public Authentication getTestAuthentication() {
        Member member = Member.of(1L, "test-user");

        MemberPrincipalDetails details = new MemberPrincipalDetails(member, null, null);
        return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
    }

    private Claims getClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public boolean isValidToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
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
