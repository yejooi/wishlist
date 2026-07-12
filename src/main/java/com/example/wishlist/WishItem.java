package com.example.wishlist;

public class WishItem {
    private Long id; // item number
    private String name; // eg) labtop
    private String category;
    private int price;

    public WishItem() {} // 기본 생성자 (빈 객체를 만들 때 호출되는 메서드, JSON->객체 변환)

    // getter, setter (필드를 읽고 쓰는 메서드)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
