package com.example.wishlist;

import org.springframework.data.repository.core.RepositoryCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication; // 지금 로그인한 사람 정보
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController // 요청 처리 + 반환값을 JSON 으로
@RequestMapping("/users") // 이 컨트롤러의 모든 주소는 /users 로 시작
public class UserController {

    private final UserRepository userRepository; // 사용자 조회용
    private final FollowRepository followRepository; // 팔로우 관계 조작용

    // 생성자 주입: 스프링이 두 repository를 자동으로 만들어 넣어줌
    public UserController(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    // 헬퍼: 토큰 속 username 으로 "지금 로그인한 User 객체"를 꺼냄
    // auth.getName() = JwtFilter 가 토큰에서 뽑아 넣어둔 username
    private User me(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요"));
    }

    // [사용자 목록] GET /users -> 나를 뺀 모든 사용자 + 내가 팔로우 중인지
    @GetMapping
    public List<UserView> list(Authentication auth) {
        User me = me(auth);
        return userRepository.findAll().stream() // 모든 사용자를 하나씩 처리
                .filter(u -> !u.getId().equals(me.getId())) // 나 자신은 목록에서 제외
                .map(u -> new UserView(
                        u.getUserName(),
                        followRepository.existsByFollowerAndFollowing(me, u) // 내가 u를 팔로우 중?
                ))
                .toList(); // 결과를 리스트로 모음
    }

    // [팔로우] POST /users/{username}/follow
    @PostMapping("/{username}/follow")
    public void follow(@PathVariable String username, Authentication auth) {
        User me = me(auth);
        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 사용자"));

        // 나 자신은 팔로우 못하게
        if (me.getId().equals(target.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신은 팔로우 불가");

        // 이미 팔로우 중이 아니면 새 관계 저장
        if (!followRepository.existsByFollowerAndFollowing(me, target)) {
            Follow f = new Follow();
            f.setFollower(me);
            f.setFollowing(target);
            followRepository.save(f); // follows 테이블에 한 행 추가
        }
    }

    // [언팔로우] DELETE /users/{username}/follow
    @DeleteMapping("/{username}/follow")
    @Transactional // deleteBy... 는 "변경 작업"이라 트랜잭션 안에서 실행되어야 함
    public void unfollow(@PathVariable String username, Authentication auth) {
        User me = me(auth);
        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 사용자"));
        followRepository.deleteByFollowerAndFollowing(me, target); // 그 관계행 삭제
    }
}
