package com.example.myapplication.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.example.myapplication.utils.AppConstants;
import com.example.myapplication.utils.Logger;

/**
 * MapManager - Quản lý các chức năng liên quan đến bản đồ và vị trí nhà hàng
 */
public class MapManager {
    private static final String TAG = "MapManager";
    private Context context;

    public MapManager(Context context) {
        this.context = context;
    }

    /**
     * Mở Google Maps để hiển thị vị trí nhà hàng
     */
    public boolean openRestaurantLocation() {
        try {
            Logger.logUserAction("OPEN_RESTAURANT_LOCATION", "User opened restaurant location");
            
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)", 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG, 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG, 
                Uri.encode(AppConstants.RESTAURANT_NAME));
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            } else {
                // Fallback to browser
                return openRestaurantLocationInBrowser();
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error opening restaurant location", e);
            Toast.makeText(context, "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Mở vị trí nhà hàng trong trình duyệt web
     */
    public boolean openRestaurantLocationInBrowser() {
        try {
            String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "Error opening restaurant location in browser", e);
            return false;
        }
    }

    /**
     * Gọi điện thoại đến nhà hàng
     */
    public boolean callRestaurant() {
        try {
            Logger.logUserAction("CALL_RESTAURANT", "User called restaurant");
            
            String phoneNumber = AppConstants.RESTAURANT_PHONE.replaceAll("[^0-9+]", "");
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "Error calling restaurant", e);
            Toast.makeText(context, "Không thể gọi điện", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Lấy chỉ đường đến nhà hàng
     */
    public boolean getDirectionsToRestaurant() {
        try {
            Logger.logUserAction("GET_DIRECTIONS", "User requested directions to restaurant");
            
            String uri = String.format("google.navigation:q=%f,%f", 
                AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            } else {
                // Fallback to browser directions
                String url = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f", 
                    AppConstants.RESTAURANT_LAT, AppConstants.RESTAURANT_LNG);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
                return true;
            }
        } catch (Exception e) {
            Logger.e(TAG, "Error getting directions", e);
            Toast.makeText(context, "Không thể lấy chỉ đường", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Kiểm tra có Google Maps app không
     */
    public boolean isGoogleMapsInstalled() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0"));
            intent.setPackage("com.google.android.apps.maps");
            return intent.resolveActivity(context.getPackageManager()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy thông tin nhà hàng
     */
    public static class RestaurantInfo {
        public String getName() { return AppConstants.RESTAURANT_NAME; }
        public String getAddress() { return AppConstants.RESTAURANT_ADDRESS; }
        public String getPhone() { return AppConstants.RESTAURANT_PHONE; }
        public String getHours() { return AppConstants.RESTAURANT_HOURS; }
        public double getLatitude() { return AppConstants.RESTAURANT_LAT; }
        public double getLongitude() { return AppConstants.RESTAURANT_LNG; }
    }

    /**
     * Lấy instance của RestaurantInfo
     */
    public RestaurantInfo getRestaurantInfo() {
        return new RestaurantInfo();
    }
}
