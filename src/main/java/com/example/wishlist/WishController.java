package com.example.wishlist;

import org.springframework.web.bind.annotation.*; // web 관련 annotation(@~) 모음
import java.util.ArrayList;
import java.util.List;

@RestController // web 요청을 처리하고, 반환값을 JSON으로 응답하는 annotation
@RequestMapping("/wishes") // 이 컨트롤러의 모든 주소는 /wishes 로 시작

public class WishController {

    // DB 대신 임시 메모리 저장소
    private final List<WishItem> items = new ArrayList<>();
    private Long nextId = 1L; // 새 아이템에 붙일 번호

    // [조회] GET /wishes -> 저장된 위시 목록 전체 반환
    @GetMapping
    public List<WishItem> getAll() {
        return items; // 리스트가 자동으로 JSON 배열로 변환
    }

    // [추가] POST /wishes -> 요청 본문을 WishItem 으로 받아 목록에 추가
    @PostMapping
    public WishItem add(@RequestBody WishItem item) { // JSON을 객체로 변환하는 annotation
        item.setId(nextId++);
        items.add(item);
        return item;
    }
}