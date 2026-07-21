package com.example.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository<Follow, Long> = Follow table 을 기본키 타입 Long 으로 다룸 -> save, findById, delete 등 기본 기능 딸려옴
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // existsBy / deleteBy / findBy + 필드명 → 쿼리를 이름으로 자동 생성. SQL 안 짜도 됨
    // 팔로워가 팔로잉을 팔로우한 행이 존재? T/F / 메서드 이름만 지으면 Spring Data 가 쿼리 자동 생성
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 관계 행을 삭제 (언팔로우)
    void deleteByFollowerAndFollowing(User Follower, User following);

    // 내가 팔로우한 관계들 전부 (팔로잉 목록)
    List<Follow> findByFollower(User follower);

    long countByFollower(User follower);
    long countByFollowing(User following);
}
