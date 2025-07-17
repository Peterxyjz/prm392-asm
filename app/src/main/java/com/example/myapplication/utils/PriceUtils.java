package com.example.myapplication.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class cho price formatting và tính toán
 */
public class PriceUtils {
    
    // Formatter cho Vietnamese currency
    private static final NumberFormat VND_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    // Constants cho phí giao hàng
    public static final double FREE_DELIVERY_THRESHOLD = 100000.0; // 100k VND
    public static final double STANDARD_DELIVERY_FEE = 25000.0;    // 25k VND

    /**
     * Format price thành Vietnamese currency
     */
    public static String formatPrice(double price) {
        try {
            return VND_FORMATTER.format(price).replace("₫", "₫");
        } catch (Exception e) {
            // Fallback formatting
            return String.format("%.0f₫", price);
        }
    }

    /**
     * Tính phí giao hàng dựa trên subtotal
     */
    public static double calculateDeliveryFee(double subtotal) {
        return subtotal >= FREE_DELIVERY_THRESHOLD ? 0.0 : STANDARD_DELIVERY_FEE;
    }

    /**
     * Tính tổng cộng (subtotal + delivery fee)
     */
    public static double calculateTotal(double subtotal) {
        return subtotal + calculateDeliveryFee(subtotal);
    }

    /**
     * Tính final total (alias for calculateTotal)
     */
    public static double calculateFinalTotal(double subtotal) {
        return calculateTotal(subtotal);
    }

    /**
     * Kiểm tra có được miễn phí giao hàng không
     */
    public static boolean isFreeDelivery(double subtotal) {
        return subtotal >= FREE_DELIVERY_THRESHOLD;
    }

    /**
     * Kiểm tra có được miễn phí giao hàng không (alias)
     */
    public static boolean isFreeShippingEligible(double subtotal) {
        return isFreeDelivery(subtotal);
    }

    /**
     * Lấy thông báo về phí giao hàng
     */
    public static String getDeliveryFeeMessage(double subtotal) {
        if (isFreeDelivery(subtotal)) {
            return "Miễn phí giao hàng";
        } else {
            double needed = FREE_DELIVERY_THRESHOLD - subtotal;
            return String.format("Thêm %s để được miễn phí giao hàng", formatPrice(needed));
        }
    }

    /**
     * Tính số tiền còn thiếu để được miễn phí ship
     */
    public static double getRemainingForFreeShipping(double subtotal) {
        if (isFreeDelivery(subtotal)) {
            return 0.0;
        }
        return FREE_DELIVERY_THRESHOLD - subtotal;
    }

    /**
     * Format delivery fee display
     */
    public static String formatDeliveryFee(double subtotal) {
        double deliveryFee = calculateDeliveryFee(subtotal);
        if (deliveryFee == 0) {
            return "Miễn phí";
        } else {
            return formatPrice(deliveryFee);
        }
    }

    /**
     * Parse price string thành double (remove currency symbol)
     */
    public static double parsePrice(String priceString) {
        try {
            // Remove currency symbols and spaces
            String cleanPrice = priceString.replaceAll("[₫,\\s]", "");
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Format percentage
     */
    public static String formatPercent(double percent) {
        return String.format("%.1f%%", percent);
    }

    /**
     * Tính discount amount
     */
    public static double calculateDiscount(double originalPrice, double discountPercent) {
        return originalPrice * (discountPercent / 100.0);
    }

    /**
     * Tính final price sau discount
     */
    public static double calculateFinalPrice(double originalPrice, double discountPercent) {
        return originalPrice - calculateDiscount(originalPrice, discountPercent);
    }

    /**
     * Round price to nearest thousand (for nice display)
     */
    public static double roundToThousand(double price) {
        return Math.round(price / 1000.0) * 1000.0;
    }

    /**
     * Check if price is valid
     */
    public static boolean isValidPrice(double price) {
        return price > 0 && price < 10000000; // Max 10 million VND
    }

    /**
     * Format price range
     */
    public static String formatPriceRange(double minPrice, double maxPrice) {
        return formatPrice(minPrice) + " - " + formatPrice(maxPrice);
    }

    /**
     * Compare prices with tolerance
     */
    public static boolean isPriceEqual(double price1, double price2) {
        return Math.abs(price1 - price2) < 0.01; // Tolerance of 1 cent
    }
}