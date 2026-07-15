package com.example.wishlist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*; // web 관련 annotation(@~) 모음
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;

@RestController // web 요청을 처리하고, 반환값을 JSON으로 응답하는 annotation
@RequestMapping("/wishes") // 이 컨트롤러의 모든 주소는 /wishes 로 시작
@CrossOrigin(origins = "http://localhost:5173")
public class WishController {

    private final WishRepository repository; // 디비 접근 담당
    private final UserRepository userRepository; // 유저 조회용

    // 생성자 주입: 스프링이 위시레포지토리를 자동으로 만들어 넣어줌
    public WishController(WishRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // 현재 로그인한 유저 객체를 꺼내는 헬퍼
    private User currentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요"));
    }

    // [조회] GET /wishes -> 저장된 위시 목록 전체 반환
    @GetMapping
    public List<WishItem> getAll(Authentication auth) {
        return repository.findByOwner(currentUser(auth)); // 리스트가 자동으로 JSON 배열로 변환
    }

    // [추가] POST /wishes -> 요청 본문을 WishItem 으로 받아 목록에 추가
    @PostMapping
    public WishItem add(@RequestBody WishItem item, Authentication auth) { // JSON을 객체로 변환하는 annotation
        item.setOwner(currentUser(auth));
        return repository.save(item);
    }

    // [단건 조회] GET /wishes/3 -> id가 3인 위시 하나 조회
    @GetMapping("/{id}")
    public WishItem getOne(@PathVariable Long id) { // PathVariable: 주소의 {id}값을 받는 annotation
        return repository.findById(id) // id로 디비에서 찾기
                .orElseThrow(() -> new RuntimeException("없는 위시 id: " + id));
    }

    // [수정] PUT /wishes/3 -> id가 3인 위시의 내용을 통째로 교체
    @PutMapping("/{id}")
    public WishItem update(@PathVariable Long id, @RequestBody WishItem newItem) {
        WishItem item = repository.findById(id) // id로 기존 것 찾고
                .orElseThrow(() -> new RuntimeException("없는 위시 id: " + id));
        item.setName(newItem.getName());
        item.setCategory(newItem.getCategory());
        item.setPrice(newItem.getPrice());
        return repository.save(item);
    }

    // [삭제] DELETE /wishes/3 -> id가 3인 위시 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id); //id로 삭제
    }

    // [상태 토글] PUT /wishes/{id}/toggle -> 위시중 <-> 구매완료 전환
    @PutMapping("/{id}/toggle")
    public WishItem toggleStatus(@PathVariable Long id) {
        WishItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 위시 id: " + id));
        // purchased면 wishing으로, 아니면 purchased로
        item.setStatus("purchased".equals(item.getStatus()) ? "wishing" : "purchased");
        return repository.save(item);
    }

    // [자동 채우기] GET /wishes/preview?url=... → 그 페이지의 제목·이미지·가격을 뽑아서 반환
    @GetMapping("/preview")
    public Map<String, String> preview(@RequestParam String url) { // @RequestParam: ?url=... 값을 받음
        Map<String, String> result = new HashMap<>();
        try {
            // url 의 HTML 을 받아옴
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0") // (userAgent=브라우저인 척, 일부 사이트 봇 차단 회피)
                    .timeout(5000)
                    .get();

            String title = meta(doc, "og:title");
            if (title == null) title = doc.title();
            String image = meta(doc, "og:image");
            String price = meta(doc, "product:price:amount");
            if (price == null) price = meta(doc, "og:price:amount");

            result.put("name", title != null ? title : "");
            result.put("imgUrl", image != null ? image : "");
            result.put("price", price != null ? price : "");
        } catch (Exception e) {
            // 실패하면 빈 값 반환 (프론트에서 직접 입력 처리)
        }
        return result;
    }

    // <meta property="og:title" content="..."> 에서 content를 뽑는 헬퍼
    private String meta(Document doc, String property) {
        Element el = doc.selectFirst("meta[property='" + property + "']");
        if (el == null) el = doc.selectFirst("meta[name='" + property + "']"); // name= 형태도 시도
        return el != null ? el.attr("content") : null;
    }
}