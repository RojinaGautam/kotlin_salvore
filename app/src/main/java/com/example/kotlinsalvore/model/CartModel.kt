package com.example.kotlinsalvore.model

data class CartItem(
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val productDesc: String,
    val image: String,
    var quantity: Int = 1
)

data class CartModel(
    val items: MutableList<CartItem> = mutableListOf()
) {
    fun addItem(product: ProductModel, quantity: Int = 1) {
        val existingItem = items.find { it.productId == product.productId }
        if (existingItem != null) {
            // Add to existing quantity
            existingItem.quantity += quantity
        } else {
            // Add new item with specified quantity
            items.add(CartItem(
                productId = product.productId,
                productName = product.productName,
                productPrice = product.productPrice,
                productDesc = product.productDesc,
                image = product.image,
                quantity = quantity
            ))
        }
    }
    
    fun addOneItem(product: ProductModel) {
        val existingItem = items.find { it.productId == product.productId }
        if (existingItem != null) {
            // Replace quantity with 1 instead of adding
            existingItem.quantity = 1
        } else {
            // Add new item with quantity 1
            items.add(CartItem(
                productId = product.productId,
                productName = product.productName,
                productPrice = product.productPrice,
                productDesc = product.productDesc,
                image = product.image,
                quantity = 1
            ))
        }
    }
    
    fun removeItem(productId: String) {
        items.removeAll { it.productId == productId }
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        val item = items.find { it.productId == productId }
        if (item != null) {
            if (quantity <= 0) {
                removeItem(productId)
            } else {
                item.quantity = quantity
            }
        }
    }
    
    fun getTotalPrice(): Double {
        return items.sumOf { it.productPrice * it.quantity }
    }
    
    fun getTotalItems(): Int {
        return items.sumOf { it.quantity }
    }
    
    fun clear() {
        items.clear()
    }
} 