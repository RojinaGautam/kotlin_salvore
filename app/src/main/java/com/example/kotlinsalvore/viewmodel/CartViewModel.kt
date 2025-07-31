package com.example.kotlinsalvore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinsalvore.model.CartModel
import com.example.kotlinsalvore.model.ProductModel
import com.example.kotlinsalvore.repository.CartRepository

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {
    
    private val _cart = MutableLiveData<CartModel>()
    val cart: LiveData<CartModel> get() = _cart
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    fun loadCart() {
        _isLoading.value = true
        cartRepository.getCart { success, message, cartModel ->
            _isLoading.value = false
            if (success && cartModel != null) {
                _cart.postValue(cartModel)
            } else {
                _cart.postValue(CartModel())
            }
        }
    }
    
    fun addToCart(product: ProductModel, quantity: Int = 1) {
        cartRepository.addToCart(product, quantity) { success, message ->
            if (success) {
                loadCart() // Reload cart after adding item
            }
        }
    }
    
    fun removeFromCart(productId: String) {
        cartRepository.removeFromCart(productId) { success, message ->
            if (success) {
                loadCart() // Reload cart after removing item
            }
        }
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        cartRepository.updateQuantity(productId, quantity) { success, message ->
            if (success) {
                loadCart() // Reload cart after updating quantity
            }
        }
    }
    
    fun clearCart() {
        cartRepository.clearCart { success, message ->
            if (success) {
                loadCart() // Reload cart after clearing
            }
        }
    }
    
    fun getTotalPrice(): Double {
        return _cart.value?.getTotalPrice() ?: 0.0
    }
    
    fun getTotalItems(): Int {
        return _cart.value?.getTotalItems() ?: 0
    }
} 