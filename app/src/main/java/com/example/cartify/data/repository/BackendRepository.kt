package com.example.cartify.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import com.example.cartify.data.model.Address
import com.example.cartify.data.model.CartItem
import com.example.cartify.data.model.Category
import com.example.cartify.data.model.Faq
import com.example.cartify.data.model.Message
import com.example.cartify.data.model.Notification
import com.example.cartify.data.model.Order
import com.example.cartify.data.model.OrderTracking
import com.example.cartify.data.model.Product
import com.example.cartify.data.model.Store
import com.example.cartify.data.model.TrackingStep
import com.example.cartify.data.remote.AddressDto
import com.example.cartify.data.remote.ApiClient
import com.example.cartify.data.remote.AuthResponseDto
import com.example.cartify.data.remote.CartItemDto
import com.example.cartify.data.remote.CategoryDto
import com.example.cartify.data.remote.FaqDto
import com.example.cartify.data.remote.LoginRequestDto
import com.example.cartify.data.remote.MessageDto
import com.example.cartify.data.remote.NotificationDto
import com.example.cartify.data.remote.OrderDto
import com.example.cartify.data.remote.OrderTrackingDto
import com.example.cartify.data.remote.ProductDto
import com.example.cartify.data.remote.SignupRequestDto
import com.example.cartify.data.remote.StoreDto
import com.example.cartify.data.remote.TrackingStepDto

object BackendRepository {
    private val api = ApiClient.service

    suspend fun login(email: String, password: String): Result<AuthResponseDto> = runCatching {
        api.login(LoginRequestDto(email = email, password = password))
    }

    suspend fun signup(name: String, email: String, password: String): Result<AuthResponseDto> = runCatching {
        api.signup(SignupRequestDto(name = name, email = email, password = password))
    }

    suspend fun fetchCategories(): Result<List<Category>> = runCatching {
        api.getCategories().map { it.toCategory() }
    }

    suspend fun fetchStores(): Result<List<Store>> = runCatching {
        api.getStores().map { it.toStore() }
    }

    suspend fun fetchStoreDetail(storeId: String): Result<Pair<Store, List<Product>>> = runCatching {
        val detail = api.getStoreDetail(storeId)
        detail.store.toStore() to detail.products.map { it.toProduct() }
    }

    suspend fun fetchProducts(): Result<List<Product>> = runCatching {
        api.getProducts().map { it.toProduct() }
    }
    
    suspend fun fetchProductDetail(productId: String): Result<Product> = runCatching {
        api.getProductDetail(productId).toProduct()
    }
    
    suspend fun fetchProductsByCategory(categoryId: String): Result<List<Product>> = runCatching {
        api.getProductsByCategory(categoryId).map { it.toProduct() }
    }

    suspend fun fetchOrders(): Result<List<Order>> = runCatching {
        api.getOrders().map { it.toOrder() }
    }
    
    suspend fun fetchOrderDetail(orderId: String): Result<Order> = runCatching {
        api.getOrderDetail(orderId).toOrder()
    }

    suspend fun placeOrder(cartItems: List<CartItem>, totalAmount: Double): Result<Order> = runCatching {
        val orderDto = OrderDto(
            id = "",
            storeName = cartItems.firstOrNull()?.product?.storeName ?: "Cartify",
            status = "Pending",
            date = "",
            totalAmount = totalAmount,
            items = cartItems.map { it.toDto() }
        )
        api.placeOrder(orderDto).toOrder()
    }

    suspend fun fetchOrderTracking(orderId: String): Result<OrderTracking> = runCatching {
        api.getOrderTracking(orderId).toOrderTracking()
    }

    suspend fun fetchMessages(): Result<List<Message>> = runCatching {
        api.getMessages().map { it.toMessage() }
    }
    
    suspend fun fetchChatMessages(vendorId: String): Result<List<Message>> = runCatching {
        api.getChatMessages(vendorId).map { it.toMessage() }
    }
    
    suspend fun sendMessage(vendorId: String, message: String): Result<Message> = runCatching {
        api.sendMessage(vendorId, message).toMessage()
    }

    suspend fun fetchAddresses(): Result<List<Address>> = runCatching {
        api.getAddresses().map { it.toAddress() }
    }

    suspend fun addAddress(title: String, fullAddress: String, isDefault: Boolean): Result<Address> = runCatching {
        val dto = AddressDto(id = "", title = title, fullAddress = fullAddress, isDefault = isDefault)
        api.addAddress(dto).toAddress()
    }

    suspend fun searchProducts(query: String): Result<List<Product>> = runCatching {
        api.searchProducts(query).map { it.toProduct() }
    }

    suspend fun fetchFavoriteProducts(): Result<List<Product>> = runCatching {
        api.getFavoriteProducts().map { it.toProduct() }
    }

    suspend fun toggleFavorite(productId: String): Result<Product> = runCatching {
        api.toggleFavorite(productId).toProduct()
    }

    suspend fun fetchNotifications(): Result<List<Notification>> = runCatching {
        api.getNotifications().map { it.toNotification() }
    }

    suspend fun fetchFaqs(): Result<List<Faq>> = runCatching {
        api.getFaqs().map { it.toFaq() }
    }

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

    private fun StoreDto.toStore(): Store = Store(
        id = id,
        name = name,
        rating = rating,
        distance = distance,
        imageUrl = "https://via.placeholder.com/150",
        bannerUrl = "https://via.placeholder.com/400x200",
        deliveryTime = deliveryTime,
        isFavorite = isFavorite,
    )

    private fun ProductDto.toProduct(): Product = Product(
        id = id,
        name = name,
        price = price,
        unit = unit,
        imageUrl = "https://via.placeholder.com/150",
        description = description,
        storeId = storeId,
        storeName = storeName,
        categoryId = categoryId,
        isFavorite = isFavorite,
    )

    private fun CartItem.toDto(): CartItemDto = CartItemDto(
        product = product.toDto(),
        quantity = quantity
    )

    private fun Product.toDto(): ProductDto = ProductDto(
        id = id,
        name = name,
        price = price,
        unit = unit,
        description = description,
        storeId = storeId,
        storeName = storeName,
        categoryId = categoryId,
        isFavorite = isFavorite
    )

    private fun CartItemDto.toCartItem(): CartItem = CartItem(
        product = product.toProduct(),
        quantity = quantity,
    )

    private fun OrderDto.toOrder(): Order = Order(
        id = id,
        storeName = storeName,
        status = status,
        date = date,
        totalAmount = totalAmount,
        items = items.map { it.toCartItem() },
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
        isMe = isMe,
    )

    private fun AddressDto.toAddress(): Address = Address(
        id = id,
        title = title,
        fullAddress = fullAddress,
        isDefault = isDefault,
        icon = Icons.Default.Work,
    )

    private fun NotificationDto.toNotification(): Notification = Notification(
        id = id,
        title = title,
        message = message,
        time = time,
        type = type
    )

    private fun FaqDto.toFaq(): Faq = Faq(
        id = id,
        question = question,
        answer = answer
    )
}
