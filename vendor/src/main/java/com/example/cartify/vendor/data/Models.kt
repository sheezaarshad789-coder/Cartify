package com.example.cartify.vendor.data

import androidx.compose.ui.graphics.vector.ImageVector

data class VendorStats(
    val totalSales: Double,
    val activeOrders: Int,
    val totalProducts: Int
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val imageUrl: String,
    val description: String,
    val storeId: String,
    val storeName: String,
    val categoryId: String,
    val isAvailable: Boolean = true,
    val stock: Int = 0
)

data class Order(
    val id: String,
    val customerName: String,
    val status: String,
    val date: String,
    val totalAmount: Double,
    val items: List<CartItem>,
    val customerAddress: String = ""
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class Category(
    val id: String,
    val name: String
)
