package com.example.myapplication.model;

import java.io.Serializable;

/**
 * Model class đại diện cho một món ăn trong ứng dụng
 * Implements Serializable để có thể truyền qua Intent
 */
public class FoodItem implements Serializable {
    private int id;              // ID duy nhất của món ăn
    private String name;         // Tên món ăn
    private String description;  // Mô tả món ăn
    private double price;        // Giá món ăn (VNĐ)
    private int imageResource;   // Resource ID của hình ảnh (cho ảnh mặc định)
    private String imageUrl;     // URL hoặc path của ảnh được upload
    private String category;     // Danh mục món ăn (Noodles, Sushi, Rice, Appetizer)
    private boolean available;   // Trạng thái còn món/hết món

    /**
     * Constructor khởi tạo FoodItem với đầy đủ thông tin
     * @param id ID của món ăn
     * @param name Tên món ăn
     * @param description Mô tả món ăn
     * @param price Giá món ăn
     * @param imageResource Resource ID của hình ảnh
     * @param category Danh mục món ăn
     */
    public FoodItem(int id, String name, String description, double price, int imageResource, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.available = true; // Mặc định là còn món
        this.imageUrl = null;  // Mặc định không có ảnh custom
    }

    /**
     * Constructor khởi tạo FoodItem với đầy đủ thông tin bao gồm available và imageUrl
     */
    public FoodItem(int id, String name, String description, double price, int imageResource, 
                   String category, boolean available, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.available = available;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter methods để truy cập và cập nhật các thuộc tính
    
    /** @return ID của món ăn */
    public int getId() { return id; }
    /** @param id ID mới của món ăn */
    public void setId(int id) { this.id = id; }

    /** @return Tên món ăn */
    public String getName() { return name; }
    /** @param name Tên mới của món ăn */
    public void setName(String name) { this.name = name; }

    /** @return Mô tả món ăn */
    public String getDescription() { return description; }
    /** @param description Mô tả mới của món ăn */
    public void setDescription(String description) { this.description = description; }

    /** @return Giá của món ăn */
    public double getPrice() { return price; }
    /** @param price Giá mới của món ăn */
    public void setPrice(double price) { this.price = price; }

    /** @return Resource ID của hình ảnh */
    public int getImageResource() { return imageResource; }
    /** @param imageResource Resource ID mới của hình ảnh */
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    /** @return URL/path của ảnh custom */
    public String getImageUrl() { return imageUrl; }
    /** @param imageUrl URL/path mới của ảnh custom */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /** @return Danh mục của món ăn */
    public String getCategory() { return category; }
    /** @param category Danh mục mới của món ăn */
    public void setCategory(String category) { this.category = category; }

    /** @return Trạng thái available của món ăn */
    public boolean isAvailable() { return available; }
    /** @param available Trạng thái available mới của món ăn */
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Kiểm tra xem món ăn có sử dụng ảnh custom không
     * @return true nếu có ảnh custom, false nếu dùng ảnh mặc định
     */
    public boolean hasCustomImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }
}
