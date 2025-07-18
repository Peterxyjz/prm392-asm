package com.example.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class để xử lý upload và lưu trữ ảnh món ăn
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final String FOOD_IMAGES_DIR = "food_images";
    private static final int MAX_IMAGE_SIZE = 1024; // Max width/height in pixels
    private static final int JPEG_QUALITY = 85;

    /**
     * Lưu ảnh từ URI vào internal storage
     * @param context Context
     * @param imageUri URI của ảnh được chọn
     * @param foodId ID của món ăn (để tạo tên file unique)
     * @return đường dẫn file đã lưu, null nếu lỗi
     */
    public static String saveImageToInternalStorage(Context context, Uri imageUri, int foodId) {
        try {
            // Tạo thư mục food_images nếu chưa có
            File foodImagesDir = new File(context.getFilesDir(), FOOD_IMAGES_DIR);
            if (!foodImagesDir.exists()) {
                foodImagesDir.mkdirs();
            }

            // Tạo tên file unique
            String fileName = "food_" + foodId + "_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(foodImagesDir, fileName);

            // Đọc và resize ảnh
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream from URI");
                return null;
            }

            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (originalBitmap == null) {
                Log.e(TAG, "Cannot decode bitmap from input stream");
                return null;
            }

            // Resize ảnh để tiết kiệm dung lượng
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_SIZE);

            // Lưu ảnh vào file
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
            outputStream.close();

            // Giải phóng memory
            if (originalBitmap != resizedBitmap) {
                originalBitmap.recycle();
            }
            resizedBitmap.recycle();

            Log.d(TAG, "Image saved successfully: " + imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Resize bitmap để giảm kích thước
     */
    private static Bitmap resizeBitmap(Bitmap original, int maxSize) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Nếu ảnh đã nhỏ hơn maxSize thì không cần resize
        if (width <= maxSize && height <= maxSize) {
            return original;
        }

        // Tính toán kích thước mới giữ nguyên tỷ lệ
        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    /**
     * Xóa ảnh cũ khi cập nhật ảnh mới
     */
    public static boolean deleteImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return true; // Không có ảnh để xóa
            }

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                boolean deleted = imageFile.delete();
                Log.d(TAG, "Delete image result: " + deleted + " for path: " + imagePath);
                return deleted;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Kiểm tra xem file ảnh có tồn tại không
     */
    public static boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return false;
        }
        
        File imageFile = new File(imagePath);
        return imageFile.exists();
    }

    /**
     * Load bitmap từ đường dẫn file
     */
    public static Bitmap loadBitmapFromPath(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return null;
            }

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                Log.w(TAG, "Image file not found: " + imagePath);
                return null;
            }

            return BitmapFactory.decodeFile(imagePath);
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap from path: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy kích thước file ảnh (để hiển thị thông tin)
     */
    public static long getImageFileSize(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return 0;
            }

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                return imageFile.length();
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting image file size: " + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Format kích thước file thành string dễ đọc
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeInBytes / 1024.0);
        } else {
            return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }
}
