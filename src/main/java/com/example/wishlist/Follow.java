package com.example.wishlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity // 이 클래스를 디비 테이블로 만듦
@Table(name = "follows", // 테이블 이름
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"})) // 같은 관계 중복 방지, 둘의 조합은 유일해야 함

public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키, 디비가 자동으로 1, 2, 3... 부여
    private Long id;

    @ManyToOne // 여러 Follow 가 한 User 를 가리킴
    @JsonIgnore // JSON 으로 내보낼 때 이 필드는 빼기 (User 엔 비번 등 민감 정보 있음)
    private User follower; // 팔로우 하는 쪽 (나)

    @ManyToOne // 마찬가지로 여러 Follow 가 한 User 를 가리킴
    @JsonIgnore
    private User following; // 팔로우 당하는 쪽 (상대)

    public Long getId() { return id; }
    public User getFollower() { return follower; }
    public void setFollower(User follower) { this.follower = follower; }
    public User getFollowing() { return following; }
    public void setFollowing(User following) { this.following = following; }
}