package com.example.kotlinsalvore.repository

import android.content.Context
import android.net.Uri
import com.example.kotlinsalvore.model.ProductModel


interface ProductRepository {

    fun addProduct(productModel: ProductModel, callback: (Boolean, String) -> Unit)

    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit)
    fun getProductById(productId: String, callback: (Boolean, String, ProductModel?) -> Unit)
    fun getAllProduct(callback: (Boolean, String, List<ProductModel?>) -> Unit)
    fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    )
    
    fun clearAllProducts(callback: (Boolean, String) -> Unit)

    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context,uri: Uri): String?

}
