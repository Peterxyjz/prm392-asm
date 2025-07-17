package com.example.myapplication.model;

import java.io.Serializable;

/**
 * Model class đại diện cho một món ăn trong giỏ hàng
 * Bao gồm thông tin món ăn và số lượng
 */
public class CartItem implements Serializable {
    private FoodItem foodItem;  // Thông tin món ăn
    private int quantity;       // Số lượng món ăn trong giỏ hàng

    /**
     * Constructor khởi tạo CartItem
     * @param foodItem Thông tin món ăn
     * @param quantity Số lượng món ăn
     */
    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    /** @return Thông tin món ăn */
    public FoodItem getFoodItem() { return foodItem; }
    /** @param foodItem Thông tin món ăn mới */
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    /** @return Số lượng món ăn */
    public int getQuantity() { return quantity; }
    /** @param quantity Số lượng mới */
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Tính tổng tiền cho món ăn này trong giỏ hàng
     * @return Tổng tiền = giá món ăn * số lượng
     */
    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }
}