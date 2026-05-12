package com.example.cartify.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class SignupRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "user" // "user" or "vendor"
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "user"
)

@Serializable
data class VendorStatsDto(
    @SerialName("total_sales") val totalSales: Double,
    @SerialName("active_orders") val activeOrders: Int,
    @SerialName("total_products") val totalProducts: Int
)

@Serializable
data class UpdateOrderStatusDto(
    val status: String
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
)

@Serializable
data class StoreDto(
    val id: String,
    val name: String,
    val rating: Double,
    val distance: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("delivery_time") val deliveryTime: String,
    @SerialName("is_favorite") val isFavorite: Boolean = false,
)

@Serializable
data class StoreDetailDto(
    val store: StoreDto,
    val products: List<ProductDto>
)

@Serializable
data class ProductDto(
    val id: String? = null,
    val name: String,
    val price: Double,
    val unit: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("store_id") val storeId: String,
    @SerialName("store_name") val storeName: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("is_favorite") val isFavorite: Boolean = false,
    @SerialName("is_available") val isAvailable: Boolean = true,
    val stock: Int = 0
)

@Serializable
data class CartItemDto(
    val product: ProductDto,
    val quantity: Int,
)

@Serializable
data class OrderDto(
    val id: String,
    @SerialName("store_name") val storeName: String,
    @SerialName("vendor_id") val vendorId: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val status: String,
    val date: String,
    @SerialName("total_amount") val totalAmount: Double,
    val items: List<CartItemDto>,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null
)

@Serializable
data class OrderTrackingDto(
    @SerialName("order_id") val orderId: String,
    val status: String,
    @SerialName("estimated_time") val estimatedTime: String,
    @SerialName("status_history") val statusHistory: List<TrackingStepDto> = emptyList()
)

@Serializable
data class TrackingStepDto(
    val status: String,
    val time: String,
    @SerialName("is_completed") val isCompleted: Boolean
)

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("sender_name") val senderName: String,
    @SerialName("last_message") val lastMessage: String,
    val time: String,
    @SerialName("is_me") val isMe: Boolean = false,
)

@Serializable
data class AddressDto(
    val id: String,
    val title: String,
    @SerialName("full_address") val fullAddress: String,
    @SerialName("is_default") val isDefault: Boolean = false,
)

@Serializable
data class FaqDto(
    val id: String,
    val question: String,
    val answer: String
)

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: String
)
