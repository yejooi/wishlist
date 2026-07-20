package com.example.wishlist;

// 사용자 목록을 보낼 때 User 통째로 (비번 포함) 보내면 안 되니까 "이름 + 내가 팔로우 중인지"만 담는 간단한 응답 전용 데이터
// record = 값만 담는 불변 데이터 클래스
public record UserView(String username, boolean following) {}
