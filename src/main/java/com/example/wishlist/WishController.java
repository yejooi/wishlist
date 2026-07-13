package com.example.wishlist;

import org.springframework.web.bind.annotation.*; // web 관련 annotation(@~) 모음
import java.util.List;

@RestController // web 요청을 처리하고, 반환값을 JSON으로 응답하는 annotation
@RequestMapping("/wishes") // 이 컨트롤러의 모든 주소는 /wishes 로 시작
@CrossOrigin(origins = "http://localhost:5173")
public class WishController {

    private final WishRepository repository; // 디비 접근 담당

    // 생성자 주입: 스프링이 위시레포지토리를 자동으로 만들어 넣어줌
    public WishController(WishRepository repository) {
        this.repository = repository;
    }

    // [조회] GET /wishes -> 저장된 위시 목록 전체 반환
    @GetMapping
    public List<WishItem> getAll() {
        return repository.findAll(); // 리스트가 자동으로 JSON 배열로 변환
    }

    // [추가] POST /wishes -> 요청 본문을 WishItem 으로 받아 목록에 추가
    @PostMapping
    public WishItem add(@RequestBody WishItem item) { // JSON을 객체로 변환하는 annotation
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
}