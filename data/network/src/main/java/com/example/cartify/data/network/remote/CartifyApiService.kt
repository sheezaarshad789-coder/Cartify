package com.example.cartify.data.network.remote

import com.example.cartify.data.network.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://cartify-backend-1rza.onrender.com/"

interface CartifyApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body payload: LoginRequestDto): AuthResponseDto

    @POST("auth/signup")
    suspend fun signup(@Body payload: SignupRequestDto): AuthResponseDto

    // Common
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("stores")
    suspend fun getStores(): List<StoreDto>

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products")
    suspend fun getProductsByCategory(@Query("category") categoryId: String): List<ProductDto>

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): List<ProductDto>

    @GET("products/favorites")
    suspend fun getFavoriteProducts(): List<ProductDto>

    @POST("products/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") productId: String): ProductDto

    // Vendor Specific Endpoints
    @GET("vendor/stats")
    suspend fun getVendorStats(): VendorStatsDto

    @GET("vendor/orders")
    suspend fun getVendorOrders(): List<OrderDto>

    @PUT("vendor/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") orderId: String,
        @Body payload: UpdateOrderStatusDto
    ): OrderDto

    @GET("vendor/products")
    suspend fun getVendorProducts(): List<ProductDto>

    @POST("vendor/products")
    suspend fun addProduct(@Body payload: ProductDto): ProductDto

    // User/Customer Specific Endpoints
    @GET("stores/{id}")
    suspend fun getStoreDetail(@Path("id") storeId: String): StoreDetailDto

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") productId: String): ProductDto

    @POST("orders")
    suspend fun placeOrder(@Body payload: OrderDto): OrderDto

    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrderDetail(@Path("id") orderId: String): OrderDto

    @GET("orders/{id}/tracking")
    suspend fun getOrderTracking(@Path("id") orderId: String): OrderTrackingDto

    @GET("messages")
    suspend fun getMessages(): List<MessageDto>
    
    @GET("messages/{vendorId}")
    suspend fun getChatMessages(@Path("vendorId") vendorId: String): List<MessageDto>

    @POST("messages/{vendorId}/send")
    suspend fun sendMessage(@Path("vendorId") vendorId: String, @Body message: String): MessageDto

    @GET("addresses")
    suspend fun getAddresses(): List<AddressDto>

    @POST("addresses")
    suspend fun addAddress(@Body payload: AddressDto): AddressDto

    @GET("notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @GET("faqs")
    suspend fun getFaqs(): List<FaqDto>
}

object ApiClient {
    val service: CartifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartifyApiService::class.java)
    }
}
