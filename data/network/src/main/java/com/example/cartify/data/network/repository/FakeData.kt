package com.example.cartify.data.network.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.mutableStateListOf
import com.example.cartify.core.common.model.*
import kotlinx.coroutines.coroutineScope

object FakeData {
    // Initializing with Fallback Data so the app is never empty
    val categories = mutableStateListOf<Category>(
        Category("1", "Fruits", Icons.Default.ShoppingCart),
        Category("2", "Vegetables", Icons.Default.Menu),
        Category("3", "Dairy", Icons.Default.Coffee),
        Category("4", "Bakery", Icons.Default.BakeryDining)
    )

    val stores = mutableStateListOf<Store>(
        Store("1", "Fresh Mart", 4.5, "1.2 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200"),
        Store("2", "Grocery Hub", 4.2, "2.5 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200")
    )

    val products = mutableStateListOf<Product>(
        Product("1", "Avocado", 4.0, "20g", "https://via.placeholder.com/150", "Fresh Avocado", "1", "Fresh Mart", "1"),
        Product("2", "Yam", 25.0, "1kg", "https://via.placeholder.com/150", "Sweet Yam", "2", "Grocery Hub", "2")
    )

    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf<Order>()
    val messages = mutableStateListOf<Message>()
    val addresses = mutableStateListOf<Address>()

    suspend fun syncFromBackend() = coroutineScope {
        // Fetch Public Data
        val categoriesResult = SupabaseRepository.fetchCategories()
        val storesResult = SupabaseRepository.fetchStores()
        val productsResult = SupabaseRepository.fetchProducts()

        categoriesResult.onSuccess { list ->
            if (list.isNotEmpty()) {
                categories.clear()
                categories.addAll(list)
            }
        }
        storesResult.onSuccess { list ->
            if (list.isNotEmpty()) {
                stores.clear()
                stores.addAll(list)
            }
        }
        productsResult.onSuccess { list ->
            if (list.isNotEmpty()) {
                products.clear()
                products.addAll(list)
            }
        }

        // Fetch Orders and Addresses
        SupabaseRepository.fetchOrders().onSuccess { list ->
            orders.clear()
            orders.addAll(list)
        }
        
        SupabaseRepository.fetchAddresses().onSuccess { list ->
            addresses.clear()
            addresses.addAll(list)
        }

        // Fetch User-Specific Data
        val userId = SupabaseRepository.getCurrentUserId()
        if (userId != null) {
            SupabaseRepository.fetchMessages(userId).onSuccess { list ->
                messages.clear()
                messages.addAll(list)
            }
        }
    }
}
