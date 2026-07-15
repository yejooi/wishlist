package com.example.wishlist;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component

/* 토큰을 만들고 검증하는 도구.

generateToken(username) → username 담아 토큰 발급(로그인 성공 시 호출)
getUsername(token) → 토큰에서 이름 꺼냄 (이때 서명·만료도 검증)
isValid(token) → 진짜/가짜 판별
비밀키(key)로 서명해서 위조 방지

*/
public class JwtUtil {
    // 토큰 서명용 비밀키
    private final SecretKey key = Keys.hmacShaKeyFor(
            "mywishlist-youcanwisheverythingyouwannaget-justbuyit!".getBytes());
    private final long EXP = 1000L * 60 * 60 * 24; // 24시간 유효

    // 토큰 발급: username 을 담아 서명
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(key)
                .compact();
    }

    // 토큰에서 username 꺼내기 (서명, 만료검증도)
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // 유효한 토큰이면 트루
    public boolean isValid(String token) {
        try { getUsername(token); return true; }
        catch (Exception e) { return false; }
    }
}