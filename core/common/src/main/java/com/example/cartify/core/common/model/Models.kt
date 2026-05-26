package com.example.cartify.core.common.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector? = null
)

data class Store(
    val id: String,
    val name: String,
    val rating: Double,
    val distance: String,
    val imageUrl: String,
    val bannerUrl: String = "",
    val description: String = "",
    val address: String = "",
    val deliveryTime: String = "20-30 mins",
    val isFavorite: Boolean = false
)

data class StoreSettings(
    val storeName: String,
    val deliveryTime: String,
    val logoUrl: String?,
    val bannerUrl: String?
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
    val rating: Double = 4.9,
    val isFavorite: Boolean = false,
    val isAvailable: Boolean = true
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

data class Order(
    val id: String,
    val storeName: String,
    val status: String,
    val date: String,
    val totalAmount: Double,
    val items: List<CartItem>,
    val customerName: String = "Unknown",
    val customerAddress: String = "No Address"
)

enum class UserRole { CUSTOMER, VENDOR, ADMIN }

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null
)

data class Message(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val time: String,
    val isMe: Boolean = false
)

data class Address(
    val id: String,
    val title: String,
    val fullAddress: String,
    val isDefault: Boolean = false,
    val icon: ImageVector? = null
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: String
)

data class TrackingStep(
    val status: String,
    val time: String,
    val isCompleted: Boolean
)

data class OrderTracking(
    val orderId: String,
    val status: String,
    val estimatedTime: String,
    val statusHistory: List<TrackingStep>
)

data class Faq(
    val id: String,
    val question: String,
    val answer: String
)
