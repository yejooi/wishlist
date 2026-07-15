package com.example.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository 를 상속하면 기본 CRUD(save, findAll, findById, delete 등)가 자동 제공
// <WishItem, Long> = 다루는 엔티티 타입, 그 기본키 (id) 타입
public interface WishRepository extends JpaRepository<WishItem, Long> {
    List<WishItem> findByOwner(User owner);
}
