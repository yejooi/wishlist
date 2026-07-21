package com.example.wishlist;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

/*회원 정보 (DB 테이블)
id, username, password(해시됨) 필드. @Entity라 users 테이블과 연결.

 */

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // 아이디 중복 금지
    private String username;

    private String password;

    private String profileImg;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) {this.password = password; }
    public String getProfileImg() { return profileImg; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
}
