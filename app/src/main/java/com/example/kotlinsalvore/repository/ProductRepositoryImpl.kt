package com.example.kotlinsalvore.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.kotlinsalvore.model.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.util.concurrent.Executors

class ProductRepositoryImpl : ProductRepository {

    private var context: Context? = null
    private val gson = Gson()
    private val productsKey = "local_products"
    
    // Local storage for products
    private val localProducts = mutableListOf<ProductModel>()
    private var nextId = 1

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dxwr7qza9",
            "api_key" to "545811657876156",
            "api_secret" to "Oo50NMS-vrURt3gETED4ibe21uo"
        )
    )

    private fun getSharedPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("ProductStorage", Context.MODE_PRIVATE)
    }

    private fun saveProductsToLocal() {
        getSharedPreferences()?.edit()?.apply {
            putString(productsKey, gson.toJson(localProducts))
            apply()
        }
    }

    private fun loadProductsFromLocal() {
        Log.d("ProductRepositoryImpl", "loadProductsFromLocal called")
        val json = getSharedPreferences()?.getString(productsKey, "[]")
        Log.d("ProductRepositoryImpl", "Loaded JSON from SharedPreferences: $json")
        
        val type = object : TypeToken<List<ProductModel>>() {}.type
        val loadedProducts = gson.fromJson<List<ProductModel>>(json, type) ?: emptyList()
        Log.d("ProductRepositoryImpl", "Parsed ${loadedProducts.size} products from JSON")
        
        localProducts.clear()
        localProducts.addAll(loadedProducts)
        
        // Set next ID to be higher than any existing ID
        nextId = if (localProducts.isNotEmpty()) {
            (localProducts.maxOfOrNull { it.productId.toIntOrNull() ?: 0 } ?: 0) + 1
        } else {
            1
        }
        
        Log.d("ProductRepositoryImpl", "Next ID will be: $nextId")
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        this.context = context
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                // ✅ Fix: Remove extensions from file name before upload
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                // ✅ Run UI updates on the Main Thread
                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun addProduct(
        productModel: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            Log.d("ProductRepositoryImpl", "addProduct called with: ${productModel.productName}")
            
            if (context == null) {
                Log.e("ProductRepositoryImpl", "Context is null!")
                callback(false, "Context is null. Please set context first.")
                return
            }
            
            Log.d("ProductRepositoryImpl", "Context is not null, proceeding...")
            
            // Generate unique ID
            val id = nextId.toString()
            productModel.productId = id
            nextId++
            
            Log.d("ProductRepositoryImpl", "Generated ID: $id")
            
            // Add to local list
            localProducts.add(productModel)
            Log.d("ProductRepositoryImpl", "Added to local list. Total products: ${localProducts.size}")
            
            // Save to local storage
            saveProductsToLocal()
            Log.d("ProductRepositoryImpl", "Saved to local storage")
            
            callback(true, "Product added successfully with ID: $id")
        } catch (e: Exception) {
            Log.e("ProductRepositoryImpl", "Error adding product: ${e.message}", e)
            callback(false, "Failed to add product: ${e.message}")
        }
    }

    override fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val product = localProducts.find { it.productId == productId }
            if (product != null) {
                localProducts.remove(product)
                saveProductsToLocal()
                callback(true, "Product deleted successfully")
            } else {
                callback(false, "Product not found")
            }
        } catch (e: Exception) {
            callback(false, "Failed to delete product: ${e.message}")
        }
    }

    override fun getProductById(
        productId: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    ) {
        try {
            loadProductsFromLocal()
            val product = localProducts.find { it.productId == productId }
            if (product != null) {
                callback(true, "Product found", product)
            } else {
                callback(false, "Product not found", null)
            }
        } catch (e: Exception) {
            callback(false, "Failed to get product: ${e.message}", null)
        }
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel?>) -> Unit) {
        try {
            Log.d("ProductRepositoryImpl", "getAllProduct called")
            
            if (context == null) {
                Log.e("ProductRepositoryImpl", "Context is null in getAllProduct!")
                callback(false, "Context is null. Please set context first.", emptyList())
                return
            }
            
            Log.d("ProductRepositoryImpl", "Context is not null, loading products...")
            loadProductsFromLocal()
            val products = localProducts.map { it as ProductModel? }
            Log.d("ProductRepositoryImpl", "Loaded ${products.size} products from local storage")
            
            // Log each product for debugging
            products.forEachIndexed { index, product ->
                Log.d("ProductRepositoryImpl", "Product $index: ${product?.productName} - $${product?.productPrice}")
            }
            
            callback(true, "Products loaded successfully. Found ${products.size} products.", products)
        } catch (e: Exception) {
            Log.e("ProductRepositoryImpl", "Error loading products: ${e.message}", e)
            callback(false, "Failed to load products: ${e.message}", emptyList())
        }
    }

    override fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            loadProductsFromLocal()
            val productIndex = localProducts.indexOfFirst { it.productId == productId }
            
            if (productIndex != -1) {
                val product = localProducts[productIndex]
                
                // Update product fields
                data["productName"]?.let { product.productName = it.toString() }
                data["productPrice"]?.let { 
                    product.productPrice = when (it) {
                        is Double -> it
                        is Int -> it.toDouble()
                        else -> it.toString().toDoubleOrNull() ?: 0.0
                    }
                }
                data["productDesc"]?.let { product.productDesc = it.toString() }
                data["image"]?.let { product.image = it.toString() }
                
                // Save to local storage
                saveProductsToLocal()
                callback(true, "Product updated successfully")
            } else {
                callback(false, "Product not found")
            }
        } catch (e: Exception) {
            callback(false, "Failed to update product: ${e.message}")
        }
    }
    
    fun setContext(context: Context) {
        Log.d("ProductRepositoryImpl", "setContext called")
        this.context = context
        loadProductsFromLocal()
        Log.d("ProductRepositoryImpl", "Context set and products loaded. Total products: ${localProducts.size}")
    }
}