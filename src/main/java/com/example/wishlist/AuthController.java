package com.example.wishlist;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173") // 프론트에서 호출 허용

/*
회원가입·로그인 담당.

/auth/signup → 비번 **해시(BCrypt)**해서 User 저장
/auth/login → 비번 확인 → 맞으면 토큰 발급해서 반환
/auth/me → (테스트) 토큰 속 이름 돌려줌
 */

public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // [회원가입] POST /auth/signup
    @PostMapping("/signup")
    public String signup(@RequestBody AuthRequest req) {
        // 빈 값이면 거부
        if (req.username() == null || req.username().isBlank()
                || req.password() == null || req.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력하세요");
        }
        // 이미 있는 아이디면 거부
        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password())); // 비번을 해시해서 저장
        userRepository.save(user);
        return "가입 완료";
     }

     // [로그인] POST /auth/login
    @PostMapping("/login")
    public java.util.Map<String, String> login(@RequestBody AuthRequest req) {
        // 빈 값이면 거부
        if (req.username() == null || req.username().isBlank()
                || req.password() == null || req.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력하세요");
        }
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "없는 아이디입니다"));
        if (!encoder.matches(req.password(), user.getPassword())) { // 입력된 비번을 같은 방식으로 해시해서 저장된 해시와 비교
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다");
        }
        String token = jwtUtil.generateToken(user.getUsername()); // 토큰 발급
        return java.util.Map.of("token", token);
    }

    // test
    @GetMapping("/me")
    public String me(java.security.Principal principal) {
        return "로그인된 사용자: " + principal.getName();
    }
}
