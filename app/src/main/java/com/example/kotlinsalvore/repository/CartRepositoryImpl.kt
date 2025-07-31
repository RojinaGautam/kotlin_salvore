package com.example.kotlinsalvore.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.kotlinsalvore.model.CartModel
import com.example.kotlinsalvore.model.ProductModel
import com.google.gson.Gson

class CartRepositoryImpl : CartRepository {
    
    private var context: Context? = null
    private val gson = Gson()
    private val cartKey = "local_cart"
    private val cart = CartModel()
    
    private fun getSharedPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("CartStorage", Context.MODE_PRIVATE)
    }
    
    private fun saveCartToLocal() {
        val json = gson.toJson(cart)
        Log.d("CartRepositoryImpl", "Saving cart to JSON: $json")
        getSharedPreferences()?.edit()?.apply {
            putString(cartKey, json)
            apply()
        }
        Log.d("CartRepositoryImpl", "Cart saved to local storage. Items: ${cart.items.size}")
        cart.items.forEach { item ->
            Log.d("CartRepositoryImpl", "Saved item: ${item.productName} (ID: ${item.productId}) - Quantity: ${item.quantity} - Price: ${item.productPrice}")
        }
    }
    
    private fun loadCartFromLocal() {
        val json = getSharedPreferences()?.getString(cartKey, "{\"items\":[]}")
        Log.d("CartRepositoryImpl", "Loading cart from JSON: $json")
        
        try {
            val loadedCart = gson.fromJson(json, CartModel::class.java) ?: CartModel()
            cart.items.clear()
            cart.items.addAll(loadedCart.items)
            Log.d("CartRepositoryImpl", "Loaded ${cart.items.size} items from cart")
            cart.items.forEach { item ->
                Log.d("CartRepositoryImpl", "Loaded item: ${item.productName} (ID: ${item.productId}) - Quantity: ${item.quantity} - Price: ${item.productPrice}")
            }
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error loading cart: ${e.message}")
            cart.items.clear()
        }
    }
    
    fun setContext(context: Context) {
        this.context = context
        loadCartFromLocal()
        Log.d("CartRepositoryImpl", "Context set and cart loaded. Items: ${cart.items.size}")
    }
    
    override fun addToCart(product: ProductModel, quantity: Int, callback: (Boolean, String) -> Unit) {
        try {
            if (context == null) {
                callback(false, "Context is null. Please set context first.")
                return
            }
            
            Log.d("CartRepositoryImpl", "Adding ${product.productName} to cart with quantity $quantity")
            Log.d("CartRepositoryImpl", "Cart before adding: ${cart.items.size} items")
            
            // Add the product with the specified quantity
            cart.addItem(product, quantity)
            
            Log.d("CartRepositoryImpl", "Cart after adding: ${cart.items.size} items")
            Log.d("CartRepositoryImpl", "Items in cart:")
            cart.items.forEach { item ->
                Log.d("CartRepositoryImpl", "- ${item.productName}: quantity ${item.quantity}")
            }
            
            saveCartToLocal()
            
            callback(true, "${product.productName} added to cart successfully!")
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error adding to cart: ${e.message}")
            callback(false, "Failed to add to cart: ${e.message}")
        }
    }
    
    override fun removeFromCart(productId: String, callback: (Boolean, String) -> Unit) {
        try {
            if (context == null) {
                callback(false, "Context is null. Please set context first.")
                return
            }
            
            val item = cart.items.find { it.productId == productId }
            if (item != null) {
                cart.removeItem(productId)
                saveCartToLocal()
                callback(true, "${item.productName} removed from cart")
            } else {
                callback(false, "Item not found in cart")
            }
        } catch (e: Exception) {
            callback(false, "Failed to remove from cart: ${e.message}")
        }
    }
    
    override fun updateQuantity(productId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        try {
            if (context == null) {
                callback(false, "Context is null. Please set context first.")
                return
            }
            
            cart.updateQuantity(productId, quantity)
            saveCartToLocal()
            callback(true, "Quantity updated successfully")
        } catch (e: Exception) {
            callback(false, "Failed to update quantity: ${e.message}")
        }
    }
    
    override fun getCart(callback: (Boolean, String, CartModel?) -> Unit) {
        try {
            if (context == null) {
                callback(false, "Context is null. Please set context first.", null)
                return
            }
            
            loadCartFromLocal()
            callback(true, "Cart loaded successfully. Items: ${cart.items.size}", cart)
        } catch (e: Exception) {
            callback(false, "Failed to load cart: ${e.message}", null)
        }
    }
    
    override fun clearCart(callback: (Boolean, String) -> Unit) {
        try {
            if (context == null) {
                callback(false, "Context is null. Please set context first.")
                return
            }
            
            cart.clear()
            saveCartToLocal()
            callback(true, "Cart cleared successfully")
        } catch (e: Exception) {
            callback(false, "Failed to clear cart: ${e.message}")
        }
    }
} 