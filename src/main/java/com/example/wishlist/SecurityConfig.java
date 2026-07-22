package com.example.wishlist;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity

/*
어떤 URL은 열고 어떤 건 막을지 정하고, 문지기를 배치.

/auth/signup, /auth/login → 누구나(permitAll)
나머지 → 인증 필요(authenticated)
JwtFilter를 보안 체인에 끼워넣음
CORS(프론트 5173 허용), 세션 안 씀(stateless), CSRF 끔 설정도 여기
 */

public class SecurityConfig { // 보안 규칙
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {this.jwtFilter = jwtFilter; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable()) // API 라 csrf 끔
                .cors(c -> c.configurationSource(corsSource())) // CORS 허용
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 안 씀
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/signup", "/auth/login").permitAll() // 가입, 로그인 누구나
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // jwt 필터 끼워넣기
        return http.build();
    }

    // 프론트에서 호출 허용
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173",
                "https://wishlist-front-roan.vercel.app"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}