package com.example.wishlist;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component

/*
모든 요청을 컨트롤러 도착 전에 가로채서 토큰 검사.

Authorization 헤더에서 토큰 꺼냄 → JwtUtil로 검증
진짜면 "이 요청은 인증됨"을 SecurityContext(메모지)에 기록
토큰 없거나 가짜면 그냥 통과(기록 안 함) → 나중에 규칙에서 막힘
 */

public class JwtFilter extends OncePerRequestFilter { // 요청마다 토큰 검사
    private final JwtUtil jwtUtil;
    public JwtFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.isValid(token)) {
                String username = jwtUtil.getUsername(token);
                // 인증됨 표시를 SecurityContext 에 저장
                var auth = new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res); // 다음 단계로 넘김
    }
}