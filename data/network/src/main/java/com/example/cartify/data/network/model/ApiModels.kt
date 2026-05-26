package com.example.cartify.data.network.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object AnyToStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AnyToString", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
    override fun deserialize(decoder: Decoder): String {
        val input = decoder as? JsonDecoder
        return if (input != null) {
            val element = input.decodeJsonElement()
            if (element is JsonPrimitive) element.content else element.toString()
        } else {
            decoder.decodeString()
        }
    }
}

@Serializable
data class CategoryDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("Name") val nameCaps: String? = null,
    @SerialName("category_name") val categoryName: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("Title") val titleCaps: String? = null
)

@Serializable
data class StoreDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String? = null,
    @SerialName("store_id") val storeId: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("Name") val nameCaps: String? = null,
    @SerialName("store_name") val storeName: String? = null,
    @SerialName("rating") val rating: Double? = 0.0,
    @SerialName("Rating") val ratingCaps: Double? = 0.0,
    @SerialName("distance") val distance: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("delivery_time") val deliveryTime: String? = null,
    @SerialName("is_favorite") val isFavorite: Boolean = false
)

@Serializable
data class ProductDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("Name") val nameCaps: String? = null,
    @SerialName("product_name") val productName: String? = null,
    @SerialName("price") val price: Double? = 0.0,
    @SerialName("unit") val unit: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("store_id") val storeId: String? = null,
    @SerialName("store_name") val storeName: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("is_favorite") val isFavorite: Boolean = false,
    @SerialName("is_available") val isAvailable: Boolean = true
)

@Serializable
data class CartItemDto(
    @SerialName("user_id") val userId: String? = null,
    @SerialName("product_id") val productId: String = "",
    @SerialName("quantity") val quantity: Int = 0,
    @SerialName("product") val product: ProductDto? = null
)

@Serializable
data class OrderDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("store_name") val storeName: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("items") val items: List<CartItemDto> = emptyList(),
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null,
    @SerialName("vendor_id") val vendorId: String? = null
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignupRequestDto(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("role") val role: String = "user"
)

@Serializable
data class AuthResponseDto(
    @SerialName("token") val token: String? = null,
    @SerialName("user") val user: UserDto? = null
)

@Serializable
data class UserDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String = "user"
)

@Serializable
data class StoreDetailDto(
    @SerialName("store") val store: StoreDto,
    @SerialName("products") val products: List<ProductDto> = emptyList()
)

@Serializable
data class TrackingStepDto(
    @SerialName("status") val status: String,
    @SerialName("time") val time: String,
    @SerialName("is_completed") val isCompleted: Boolean = false
)

@Serializable
data class OrderTrackingDto(
    @SerialName("order_id") val orderId: String,
    @SerialName("status") val status: String,
    @SerialName("estimated_time") val estimatedTime: String = "",
    @SerialName("status_history") val statusHistory: List<TrackingStepDto> = emptyList()
)

@Serializable
data class NotificationDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("time") val time: String,
    @SerialName("type") val type: String = "info"
)

@Serializable
data class FaqDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String,
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String
)

@Serializable
data class AddressDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("title") val title: String,
    @SerialName("full_address") val fullAddress: String,
    @SerialName("is_default") val isDefault: Boolean = false
)

@Serializable
data class MessageDto(
    @Serializable(with = AnyToStringSerializer::class) @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("sender_name") val senderName: String,
    @SerialName("last_message") val lastMessage: String,
    @SerialName("time") val time: String,
    @SerialName("is_me") val isMe: Boolean = false
)

@Serializable
data class VendorStatsDto(
    @SerialName("total_sales") val totalSales: Double = 0.0,
    @SerialName("active_orders") val activeOrders: Int = 0,
    @SerialName("total_products") val totalProducts: Int = 0
)

@Serializable
data class UpdateOrderStatusDto(
    @SerialName("status") val status: String
)
