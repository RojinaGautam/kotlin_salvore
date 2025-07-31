package com.example.kotlinsalvore.repository

import com.example.kotlinsalvore.model.CartModel
import com.example.kotlinsalvore.model.ProductModel

interface CartRepository {
    fun addToCart(product: ProductModel, quantity: Int, callback: (Boolean, String) -> Unit)
    fun removeFromCart(productId: String, callback: (Boolean, String) -> Unit)
    fun updateQuantity(productId: String, quantity: Int, callback: (Boolean, String) -> Unit)
    fun getCart(callback: (Boolean, String, CartModel?) -> Unit)
    fun clearCart(callback: (Boolean, String) -> Unit)
} 