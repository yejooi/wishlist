package com.example.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/*
회원 DB 접근
findByUsername() 등 DB 조회. JpaRepository 상속해서 save·find 자동 제공.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    // 아이디로 사용자 찾기 (spring data 가 메서드 이름 보고 쿼리 자동 생성
    Optional<User> findByUsername(String username);
}
