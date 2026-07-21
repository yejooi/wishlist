package com.example.wishlist;

public record ProfileView(
        String username,
        String profileImg,
        long followingCount,
        long followerCount,
        boolean following
) {}