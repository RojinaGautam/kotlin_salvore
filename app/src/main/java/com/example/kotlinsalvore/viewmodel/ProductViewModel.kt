package com.example.kotlinsalvore.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.kotlinsalvore.model.ProductModel
import com.example.kotlinsalvore.repository.ProductRepository

class ProductViewModel(val repo: ProductRepository) : ViewModel() {


    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }

    fun addProduct(productModel: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.addProduct(productModel, callback)
    }

    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProduct(productId, callback)
    }

    private val _products = MutableLiveData<ProductModel?>()
    val products: LiveData<ProductModel?> get() = _products

    private val _allProducts = MutableLiveData<List<ProductModel?>>()
    val allProducts: LiveData<List<ProductModel?>> get() = _allProducts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getProductById(productId: String) {
        repo.getProductById(productId) { success, message, products ->
            if (success && products != null) {
                _products.postValue(products)
            } else {
                _products.postValue(null)

            }
        }
    }


    fun getAllProduct() {
        _isLoading.value = true
        repo.getAllProduct { success, message, products ->
            if (success && products != null) {
                _allProducts.postValue(products)
                _isLoading.value = false
            } else {
                _isLoading.value = false
                _allProducts.postValue(emptyList())

            }
        }
    }

    fun updateProduct(
        productId: String, data: MutableMap<String, Any?>, callback: (Boolean, String) -> Unit
    ) {
        repo.updateProduct(productId, data, callback)
    }
    
    fun clearAllProducts(callback: (Boolean, String) -> Unit) {
        repo.clearAllProducts { success, message ->
            if (success) {
                getAllProduct() // Refresh the list after clearing
            }
            callback(success, message)
        }
    }
}