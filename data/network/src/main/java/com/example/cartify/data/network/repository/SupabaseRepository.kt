package com.example.cartify.data.network.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.cartify.core.common.model.Address
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Category
import com.example.cartify.core.common.model.Message
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.Store
import com.example.cartify.data.network.model.*
import com.example.cartify.data.network.remote.SupabaseManager
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.eq
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import com.example.cartify.core.common.model.Order as DomainOrder

/**
 * Unified Supabase Repository for Cartify.
 * Fixed for Supabase 2.5.0 compatibility and Domain model alignment.
 */
object SupabaseRepository {

    private val client = SupabaseManager.client

    fun getCurrentUserId(): String? = try {
        client.auth.currentUserOrNull()?.id
    } catch (_: Exception) {
        null
    }

    // --- Authentication ---
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

    // --- Data Fetching ---
    suspend fun fetchCategories(): Result<List<Category>> = runCatching {
        client.postgrest.from("categories").select().decodeList<CategoryDto>().map { it.toCategory() }
    }

    suspend fun fetchStores(): Result<List<Store>> = runCatching {
        client.postgrest.from("stores").select().decodeList<StoreDto>().map { it.toStore() }
    }

    suspend fun fetchProducts(): Result<List<Product>> = runCatching {
        val list = client.postgrest.from("products").select().decodeList<ProductDto>()
        list.map { it.toProduct() }
    }

    suspend fun fetchOrders(): Result<List<DomainOrder>> = runCatching {
        val userId = getCurrentUserId() ?: return@runCatching emptyList<DomainOrder>()
        client.postgrest.from("orders").select {
            filter { 
                eq("user_id", userId) 
            }
        }.decodeList<OrderDto>().map { it.toOrder() }
    }

    suspend fun fetchAddresses(): Result<List<Address>> = runCatching {
        val userId = getCurrentUserId() ?: return@runCatching emptyList<Address>()
        client.postgrest.from("addresses").select {
            filter { 
                eq("user_id", userId) 
            }
        }.decodeList<AddressDto>().map { it.toAddress() }
    }

    suspend fun fetchMessages(userId: String): Result<List<Message>> = runCatching {
        val list = client.postgrest.from("messages").select {
            filter { 
                eq("user_id", userId) 
            }
        }.decodeList<MessageDto>()
        list.map { it.toMessage() }
    }

    // --- Vendor Specific ---

    suspend fun fetchVendorStats(vendorId: String): Result<VendorStatsDto> = runCatching {
        val productsResult = client.postgrest.from("products").select {
            filter { eq("store_id", vendorId) }
        }.decodeList<ProductDto>()
        
        val ordersResult = client.postgrest.from("orders").select {
            filter { eq("vendor_id", vendorId) }
        }.decodeList<OrderDto>()
        
        val totalSales = ordersResult.filter { it.status.lowercase() == "completed" }.sumOf { it.totalAmount }
        val activeOrders = ordersResult.count { it.status.lowercase() != "completed" && it.status.lowercase() != "cancelled" }
        
        VendorStatsDto(
            totalSales = totalSales,
            activeOrders = activeOrders,
            totalProducts = productsResult.size
        )
    }

    suspend fun fetchVendorOrders(vendorId: String): Result<List<DomainOrder>> = runCatching {
        client.postgrest.from("orders").select {
            filter { eq("vendor_id", vendorId) }
        }.decodeList<OrderDto>().map { it.toOrder() }
    }

    suspend fun fetchStoreById(storeId: String): Result<Store> = runCatching {
        client.postgrest.from("stores").select {
            filter {
                or {
                    eq("id", storeId)
                    eq("user_id", storeId)
                }
            }
        }.decodeSingle<StoreDto>().toStore()
    }

    suspend fun fetchVendorProducts(vendorId: String): Result<List<Product>> = runCatching {
        client.postgrest.from("products").select {
            filter { eq("store_id", vendorId) }
        }.decodeList<ProductDto>().map { it.toProduct() }
    }

    fun observeVendorOrders(vendorId: String): Flow<List<DomainOrder>> {
        return client.postgrest.from("orders").selectAsFlow<OrderDto>(
            primaryKey = OrderDto::id,
            filter = {
                eq("vendor_id", vendorId)
            }
        ).map { list -> list.map { it.toOrder() } }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = runCatching {
        client.postgrest.from("orders").update(
            buildJsonObject { put("status", newStatus) }
        ) {
            filter { eq("id", orderId) }
        }
    }

    suspend fun addProduct(product: ProductDto, imageBytes: ByteArray?): Result<Unit> = runCatching {
        var finalProduct = product
        if (imageBytes != null) {
            val fileName = "${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("products")
            bucket.upload(fileName, imageBytes)
            val publicUrl = bucket.publicUrl(fileName)
            finalProduct = product.copy(imageUrl = publicUrl)
        }
        client.postgrest.from("products").insert(finalProduct)
    }

    suspend fun updateProduct(product: ProductDto, imageBytes: ByteArray?): Result<Unit> = runCatching {
        var finalProduct = product
        if (imageBytes != null) {
            val fileName = "${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("products")
            bucket.upload(fileName, imageBytes)
            val publicUrl = bucket.publicUrl(fileName)
            finalProduct = product.copy(imageUrl = publicUrl)
        }
        client.postgrest.from("products").update(finalProduct) {
            filter { eq("id", product.id ?: "") }
        }
    }

    suspend fun updateStore(storeDto: StoreDto, logoBytes: ByteArray?, bannerBytes: ByteArray?): Result<Unit> = runCatching {
        var finalStore = storeDto
        if (logoBytes != null) {
            val fileName = "logo_${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("stores")
            bucket.upload(fileName, logoBytes)
            finalStore = finalStore.copy(imageUrl = bucket.publicUrl(fileName))
        }
        if (bannerBytes != null) {
            val fileName = "banner_${UUID.randomUUID()}.jpg"
            val bucket = client.storage.from("stores")
            bucket.upload(fileName, bannerBytes)
            finalStore = finalStore.copy(bannerUrl = bucket.publicUrl(fileName))
        }
        client.postgrest.from("stores").update(finalStore) {
            filter { eq("id", storeDto.id ?: "") }
        }
    }

    suspend fun toggleProductAvailability(productId: String, isAvailable: Boolean): Result<Unit> = runCatching {
        client.postgrest.from("products").update(
            buildJsonObject { put("is_available", isAvailable) }
        ) {
            filter { eq("id", productId) }
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        client.postgrest.from("products").delete {
            filter { eq("id", productId) }
        }
    }

    // --- Mappers ---
    private fun CategoryDto.toCategory() = Category(id = id ?: "", name = name ?: "Grocery", icon = Icons.Default.ShoppingCart)
    
    private fun StoreDto.toStore() = Store(
        id = id ?: "", name = name ?: "Store", rating = rating ?: 4.5, 
        distance = distance ?: "1km", imageUrl = imageUrl ?: "", 
        bannerUrl = bannerUrl ?: "", description = description ?: "", address = address ?: ""
    )

    private fun ProductDto.toProduct() = Product(
        id = id ?: productId ?: "", name = name ?: "Product", price = price ?: 0.0, 
        unit = unit ?: "kg", imageUrl = imageUrl ?: "", description = description ?: "", 
        storeId = storeId ?: "", storeName = storeName ?: "", categoryId = categoryId ?: ""
    )

    private fun OrderDto.toOrder(): DomainOrder = DomainOrder(
        id = id, storeName = storeName, status = status, date = date, 
        totalAmount = totalAmount, items = items.map { it.toCartItem() }, 
        customerName = customerName ?: "Guest", customerAddress = customerAddress ?: "No Address"
    )

    private fun CartItemDto.toCartItem() = CartItem(
        product = product?.toProduct() ?: Product(productId, "Product", 0.0, "kg", "", "", "", "", ""),
        quantity = quantity
    )

    private fun AddressDto.toAddress() = Address(id = id, title = title, fullAddress = fullAddress, isDefault = isDefault)

    private fun MessageDto.toMessage() = Message(id = id, senderName = senderName, lastMessage = lastMessage, time = time, isMe = isMe)

}
