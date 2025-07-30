package com.example.kotlinsalvore.model

data class ProductModel(
    var productId: String = " ",
    var productName: String = " ",
    var productPrice: Double =  0.0,
    val productDesc: String = "",
    var image:String ="",
)

