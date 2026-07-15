package com.example.wishlist;

public record AuthRequest(String username, String password) {}
// 회원가입 / 로그인 요청 담는 그릇 (레코드는 간단한 데이터 클래스)