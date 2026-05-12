package com.example.cartify.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.cartify.data.model.*
import com.example.cartify.data.remote.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map as flowMap
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

object SupabaseRepository {
    
    private val client = SupabaseManager.client

    // --- Auth ---
    suspend fun login(emailInput: String, passwordInput: String): Result<Unit> = runCatching {
        client.auth.signInWith(Email) {
            email = emailInput
            password = passwordInput
        }
    }

    suspend fun signup(nameInput: String, emailInput: String, passwordInput: String, roleInput: String): Result<Unit> = runCatching {
        client.auth.signUpWith(Email) {
            email = emailInput
            password = passwordInput
            data = buildJsonObject {
                put("name", nameInput)
                put("role", roleInput)
            }
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        client.auth.signOut()
    }

    // --- Common Data ---
    suspend fun fetchCategories(): Result<List<Category>> = runCatching {
        val list = client.postgrest.from("categories").select().decodeList<CategoryDto>()
        list.map { it.toCategory() }
    }

    suspend fun fetchStores(): Result<List<Store>> = runCatching {
        val list = client.postgrest.from("stores").select().decodeList<StoreDto>()
        list.map { it.toStore() }
    }

    suspend fun fetchStoreById(storeId: String): Result<Store> = runCatching {
        client.postgrest.from("stores").select {
            filter { eq("id", storeId) }
        }.decodeSingle<StoreDto>().toStore()
    }

    suspend fun fetchStoreDetail(storeId: String): Result<Pair<Store, List<Product>>> = runCatching {
        val storeDto = client.postgrest.from("stores").select {
            filter { eq("id", storeId) }
        }.decodeSingle<StoreDto>()
        
        val productsList = client.postgrest.from("products").select {
            filter { eq("store_id", storeId) }
        }.decodeList<ProductDto>()
        
        storeDto.toStore() to productsList.map { it.toProduct() }
    }

    suspend fun fetchProducts(): Result<List<Product>> = runCatching {
        val list = client.postgrest.from("products").select().decodeList<ProductDto>()
        list.map { it.toProduct() }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        val list = client.postgrest.from("products").select {
            filter { ilike("name", "%$query%") }
        }.decodeList<ProductDto>()
        list.map { it.toProduct() }
    }

    suspend fun fetchFavoriteProducts(): Result<List<Product>> = runCatching {
        val list = client.postgrest.from("products").select {
            filter { eq("is_favorite", true) }
        }.decodeList<ProductDto>()
        list.map { it.toProduct() }
    }

    suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Result<Unit> = runCatching {
        client.postgrest.from("products").update(
            buildJsonObject { put("is_favorite", !isFavorite) }
        ) {
            filter { eq("id", productId) }
        }
    }

    // --- Checkout & Orders ---
    suspend fun placeOrder(cartItems: List<CartItem>, totalAmount: Double, userId: String): Result<Order> = runCatching {
        val storeName = cartItems.firstOrNull()?.product?.storeName ?: "Cartify"
        val vendorId = cartItems.firstOrNull()?.product?.storeId ?: ""
        
        val orderDto = OrderDto(
            id = UUID.randomUUID().toString(),
            storeName = storeName,
            status = "Pending",
            date = "Today",
            totalAmount = totalAmount,
            items = cartItems.map { CartItemDto(it.product.toDto(), it.quantity) },
            customerName = client.auth.currentUserOrNull()?.userMetadata?.get("name")?.toString() ?: "User"
        )
        
        client.postgrest.from("orders").insert(orderDto)
        orderDto.toOrder()
    }

    suspend fun fetchOrders(): Result<List<Order>> = runCatching {
        val userId = client.auth.currentUserOrNull()?.id
        val list = client.postgrest.from("orders").select {
            if (userId != null) {
                filter { eq("user_id", userId) }
            }
        }.decodeList<OrderDto>()
        list.map { it.toOrder() }
    }

    suspend fun fetchOrderTracking(orderId: String): Result<OrderTracking> = runCatching {
        client.postgrest.from("order_tracking").select {
            filter { eq("order_id", orderId) }
        }.decodeSingle<OrderTrackingDto>().toOrderTracking()
    }

    // --- Addresses ---
    suspend fun fetchAddresses(): Result<List<Address>> = runCatching {
        val list = client.postgrest.from("addresses").select().decodeList<AddressDto>()
        list.map { it.toAddress() }
    }

    suspend fun addAddress(title: String, fullAddress: String, isDefault: Boolean): Result<Address> = runCatching {
        val dto = AddressDto(id = UUID.randomUUID().toString(), title = title, fullAddress = fullAddress, isDefault = isDefault)
        client.postgrest.from("addresses").insert(dto)
        dto.toAddress()
    }

    // --- Notifications ---
    suspend fun fetchNotifications(): Result<List<Notification>> = runCatching {
        val list = client.postgrest.from("notifications").select().decodeList<NotificationDto>()
        list.map { it.toNotification() }
    }

    // --- Messages ---
    suspend fun fetchMessages(): Result<List<Message>> = runCatching {
        val list = client.postgrest.from("messages").select().decodeList<MessageDto>()
        list.map { it.toMessage() }
    }

    // --- Vendor Core ---
    suspend fun fetchVendorStats(vendorId: String): Result<VendorStatsDto> = runCatching {
        val orders = client.postgrest.from("orders")
            .select { filter { eq("vendor_id", vendorId) } }
            .decodeList<OrderDto>()
        
        val totalSalesValue = orders.filter { it.status == "Delivered" }.sumOf { it.totalAmount }
        val activeCount = orders.count { it.status != "Delivered" && it.status != "Cancelled" }
        
        val products = client.postgrest.from("products")
            .select { filter { eq("store_id", vendorId) } }
            .decodeList<ProductDto>()
        
        VendorStatsDto(totalSalesValue, activeCount, products.size)
    }

    fun observeVendorOrders(vendorId: String): Flow<List<Order>> {
        val myChannel = client.channel("vendor_orders_realtime")
        return myChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "orders"
        }.flowMap { 
            fetchVendorOrders(vendorId).getOrDefault(emptyList())
        }
    }

    suspend fun fetchVendorOrders(vendorId: String): Result<List<Order>> = runCatching {
        val list = client.postgrest.from("orders")
            .select { filter { eq("vendor_id", vendorId) } }
            .decodeList<OrderDto>()
        list.map { it.toOrder() }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = runCatching {
        client.postgrest.from("orders").update(
            buildJsonObject { put("status", newStatus) }
        ) {
            filter { eq("id", orderId) }
        }
    }

    // --- Product Management ---
    suspend fun fetchVendorProducts(vendorId: String): Result<List<Product>> = runCatching {
        val list = client.postgrest.from("products").select {
            filter { eq("store_id", vendorId) }
        }.decodeList<ProductDto>()
        list.map { it.toProduct() }
    }

    suspend fun addProduct(product: ProductDto, imageBytes: ByteArray?): Result<Unit> = runCatching {
        var finalImageUrl = "https://via.placeholder.com/150"
        if (imageBytes != null) {
            val fileName = "products/${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("product-images")
            bucket.upload(fileName, imageBytes)
            finalImageUrl = bucket.publicUrl(fileName)
        }
        val productWithImage = product.copy(imageUrl = finalImageUrl)
        client.postgrest.from("products").insert(productWithImage)
    }
    
    suspend fun updateProduct(product: ProductDto, imageBytes: ByteArray?): Result<Unit> = runCatching {
        var finalImageUrl = product.imageUrl
        if (imageBytes != null) {
            val fileName = "products/${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("product-images")
            bucket.upload(fileName, imageBytes)
            finalImageUrl = bucket.publicUrl(fileName)
        }
        val productToUpdate = product.copy(imageUrl = finalImageUrl)
        client.postgrest.from("products").update(productToUpdate) {
            filter { eq("id", product.id ?: "") }
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        client.postgrest.from("products").delete {
            filter { eq("id", productId) }
        }
    }

    suspend fun toggleProductAvailability(productId: String, isAvailable: Boolean): Result<Unit> = runCatching {
        client.postgrest.from("products").update(
            buildJsonObject { put("is_available", isAvailable) }
        ) {
            filter { eq("id", productId) }
        }
    }

    // --- Store Management ---
    suspend fun updateStore(storeDto: StoreDto, logoBytes: ByteArray?, bannerBytes: ByteArray?): Result<Unit> = runCatching {
        var finalLogoUrl = storeDto.imageUrl
        var finalBannerUrl = "" // Need banner field in StoreDto or handle separately

        if (logoBytes != null) {
            val fileName = "stores/logo_${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("store-assets")
            bucket.upload(fileName, logoBytes)
            finalLogoUrl = bucket.publicUrl(fileName)
        }
        
        // This is a simplified update, usually you'd update specific columns
        client.postgrest.from("stores").update(
            buildJsonObject { 
                put("name", storeDto.name)
                put("image_url", finalLogoUrl)
            }
        ) {
            filter { eq("id", storeDto.id) }
        }
    }

    // --- Mappers ---
    private fun OrderDto.toOrder(): Order = Order(
        id = id,
        storeName = storeName,
        status = status,
        date = date,
        totalAmount = totalAmount,
        items = items.map { it.toCartItem() },
        customerName = customerName ?: "Guest",
        customerAddress = customerAddress ?: "Main Street, City"
    )

    private fun CartItemDto.toCartItem(): CartItem = CartItem(
        product = product.toProduct(),
        quantity = quantity
    )

    private fun ProductDto.toProduct(): Product = Product(
        id = id ?: "",
        name = name,
        price = price,
        unit = unit,
        imageUrl = imageUrl ?: "https://via.placeholder.com/150",
        description = description,
        storeId = storeId,
        storeName = storeName,
        categoryId = categoryId,
        isFavorite = isFavorite,
        isAvailable = isAvailable
    )

    private fun Product.toDto(): ProductDto = ProductDto(
        id = id,
        name = name,
        price = price,
        unit = unit,
        description = description,
        imageUrl = imageUrl,
        storeId = storeId,
        storeName = storeName,
        categoryId = categoryId,
        isFavorite = isFavorite,
        isAvailable = isAvailable
    )

    private fun StoreDto.toStore(): Store = Store(
        id = id,
        name = name,
        rating = rating,
        distance = distance,
        imageUrl = imageUrl ?: "https://via.placeholder.com/150",
        bannerUrl = "https://via.placeholder.com/400x200",
        deliveryTime = deliveryTime,
        isFavorite = isFavorite
    )

    private fun AddressDto.toAddress(): Address = Address(
        id = id,
        title = title,
        fullAddress = fullAddress,
        isDefault = isDefault,
        icon = Icons.Default.Work
    )

    private fun NotificationDto.toNotification(): Notification = Notification(
        id = id,
        title = title,
        message = message,
        time = time,
        type = type
    )

    private fun OrderTrackingDto.toOrderTracking(): OrderTracking = OrderTracking(
        orderId = orderId,
        status = status,
        estimatedTime = estimatedTime,
        statusHistory = statusHistory.map { it.toTrackingStep() }
    )

    private fun TrackingStepDto.toTrackingStep(): TrackingStep = TrackingStep(
        status = status,
        time = time,
        isCompleted = isCompleted
    )

    private fun MessageDto.toMessage(): Message = Message(
        id = id,
        senderName = senderName,
        lastMessage = lastMessage,
        time = time,
        isMe = isMe
    )
    
    private fun CategoryDto.toCategory(): Category {
        val icon = when (name.lowercase()) {
            "fruits" -> Icons.Default.ShoppingCart
            "vegetables" -> Icons.Default.Menu
            "dairy" -> Icons.Default.Coffee
            "bakery" -> Icons.Default.BakeryDining
            "meat" -> Icons.Default.Restaurant
            "snacks" -> Icons.Default.Fastfood
            else -> Icons.Default.ShoppingCart
        }
        return Category(id = id, name = name, icon = icon)
    }
}
