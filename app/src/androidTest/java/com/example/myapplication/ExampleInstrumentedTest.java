package com.example.myapplication;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.manager.FoodDataManager;
import com.example.myapplication.manager.UserManager;
import com.example.myapplication.model.FoodItem;
import com.example.myapplication.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 * Updated for multi-user support
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }

    @Test
    public void testFoodDataManager() {
        // Test if food data is loaded correctly
        assertTrue("Should have food items", FoodDataManager.getAllFoodItems().size() > 0);
        assertEquals("Should have 7 food items", 7, FoodDataManager.getAllFoodItems().size());
        
        FoodItem ramen = FoodDataManager.getFoodItemById(1);
        assertNotNull("Ramen should exist", ramen);
        assertEquals("Ramen name should match", "Ramen Tonkotsu", ramen.getName());
        assertEquals("Ramen price should be 85000", 85000.0, ramen.getPrice(), 0.01);
    }

    @Test
    public void testCartManager() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CartManager cartManager = CartManager.getInstance(appContext);
        
        // Clear cart first
        cartManager.clearCart();
        assertEquals("Cart should be empty", 0, cartManager.getCartItemCount());
        
        // Add item to cart
        FoodItem ramen = FoodDataManager.getFoodItemById(1);
        cartManager.addToCart(ramen, 2);
        
        assertEquals("Cart should have 2 items", 2, cartManager.getCartItemCount());
        assertEquals("Total should be 170000", 170000.0, cartManager.getTotalPrice(), 0.01);
        
        // Clear cart again
        cartManager.clearCart();
        assertEquals("Cart should be empty after clear", 0, cartManager.getCartItemCount());
    }

    @Test
    public void testUserManagerMultiUser() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserManager userManager = UserManager.getInstance(appContext);
        
        // Test first user
        assertTrue("Should be able to login with first username", userManager.login("minat"));
        assertTrue("Should be logged in", userManager.isLoggedIn());
        
        User currentUser = userManager.getCurrentUser();
        assertNotNull("Current user should not be null", currentUser);
        assertEquals("Username should match", "minat", currentUser.getUsername());
        assertEquals("Full name should default to username", "minat", currentUser.getFullName());
        
        // Logout
        userManager.logout();
        assertFalse("Should be logged out", userManager.isLoggedIn());
        
        // Test second user - should work now
        assertTrue("Should be able to login with different username", userManager.login("duc"));
        assertTrue("Should be logged in with second user", userManager.isLoggedIn());
        
        User secondUser = userManager.getCurrentUser();
        assertNotNull("Second user should not be null", secondUser);
        assertEquals("Second username should match", "duc", secondUser.getUsername());
        
        // Logout second user
        userManager.logout();
        assertFalse("Should be logged out", userManager.isLoggedIn());
        
        // Login back with first user - should remember info
        assertTrue("Should be able to login back with first user", userManager.login("minat"));
        User firstUserAgain = userManager.getCurrentUser();
        assertEquals("Should remember first user's username", "minat", firstUserAgain.getUsername());
    }

    @Test
    public void testUserDataPersistence() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserManager userManager = UserManager.getInstance(appContext);
        
        // Login with user and update info
        userManager.login("testuser");
        userManager.updateUserInfo("Test Full Name", "Test Address", "0123456789");
        
        // Check info is saved
        User user = userManager.getCurrentUser();
        assertEquals("Full name should be saved", "Test Full Name", user.getFullName());
        assertEquals("Address should be saved", "Test Address", user.getAddress());
        assertEquals("Phone should be saved", "0123456789", user.getPhone());
        
        // Logout and login again - should remember info
        userManager.logout();
        userManager.login("testuser");
        
        User userAgain = userManager.getCurrentUser();
        assertEquals("Should remember full name", "Test Full Name", userAgain.getFullName());
        assertEquals("Should remember address", "Test Address", userAgain.getAddress());
        assertEquals("Should remember phone", "0123456789", userAgain.getPhone());
    }
}