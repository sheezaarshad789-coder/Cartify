package com.example.cartify.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class SignupRequestDto(
    val name: String,
    val email: String,
    val password: String,
)

data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
)

data class CategoryDto(
    val id: String,
    val name: String,
)

data class StoreDto(
    val id: String,
    val name: String,
    val rating: Double,
    val distance: String,
    @SerializedName("delivery_time") val deliveryTime: String,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
)

data class StoreDetailDto(
    val store: StoreDto,
    val products: List<ProductDto>
)

data class ProductDto(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val description: String,
    @SerializedName("store_id") val storeId: String,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
)

data class CartItemDto(
    val product: ProductDto,
    val quantity: Int,
)

data class OrderDto(
    val id: String,
    @SerializedName("store_name") val storeName: String,
    val status: String,
    val date: String,
    @SerializedName("total_amount") val totalAmount: Double,
    val items: List<CartItemDto>,
)

data class OrderTrackingDto(
    val orderId: String,
    val status: String,
    val estimatedTime: String,
    val statusHistory: List<TrackingStepDto>
)

data class TrackingStepDto(
    val status: String,
    val time: String,
    val isCompleted: Boolean
)

data class MessageDto(
    val id: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("last_message") val lastMessage: String,
    val time: String,
    @SerializedName("is_me") val isMe: Boolean = false,
)

data class AddressDto(
    val id: String,
    val title: String,
    @SerializedName("full_address") val fullAddress: String,
    @SerializedName("is_default") val isDefault: Boolean = false,
)

data class FaqDto(
    val id: String,
    val question: String,
    val answer: String
)

data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: String
)
