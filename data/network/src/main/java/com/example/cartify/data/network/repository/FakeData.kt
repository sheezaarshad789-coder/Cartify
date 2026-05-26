package com.example.cartify.data.network.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.mutableStateListOf
import com.example.cartify.core.common.model.*
import kotlinx.coroutines.coroutineScope

/**
 * Enterprise-Grade Fake Data Orchestrator.
 * Orchestrates the synchronization of backend data into UI-ready state-aware lists.
 * Strictly aligned with project-wide models to eliminate compilation errors.
 */
object FakeData {
    // State-aware lists for the UI to observe
    val categories = mutableStateListOf<Category>(
        Category("1", "Fruits", Icons.Default.ShoppingCart),
        Category("2", "Vegetables", Icons.Default.Menu),
        Category("3", "Dairy", Icons.Default.Coffee)
    )

    val stores = mutableStateListOf<Store>(
        Store("1", "Fresh Mart", 4.5, "1.2 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200")
    )

    val products = mutableStateListOf<Product>(
        Product("1", "Organic Avocado", 450.0, "kg", "https://via.placeholder.com/150", "Premium Hass Avocado", "101", "Green Grocers", "produce")
    )

    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf<Order>()
    val messages = mutableStateListOf<Message>()
    val addresses = mutableStateListOf<Address>()

    /**
     * Synchronizes local state with the Supabase Backend.
     * Uses explicit type mapping to resolve compiler inference issues.
     */
    suspend fun syncFromBackend() = coroutineScope {
        // Sync Public Catalog Data
        SupabaseRepository.fetchCategories().onSuccess { list: List<Category> ->
            if (list.isNotEmpty()) {
                categories.clear()
                categories.addAll(list)
            }
        }
        
        SupabaseRepository.fetchStores().onSuccess { list: List<Store> ->
            if (list.isNotEmpty()) {
                stores.clear()
                stores.addAll(list)
            }
        }

        SupabaseRepository.fetchProducts().onSuccess { list: List<Product> ->
            if (list.isNotEmpty()) {
                products.clear()
                products.addAll(list)
            }
        }

        // Sync Private User Data
        SupabaseRepository.fetchOrders().onSuccess { list: List<Order> ->
            orders.clear()
            orders.addAll(list)
        }
        
        SupabaseRepository.fetchAddresses().onSuccess { list: List<Address> ->
            addresses.clear()
            addresses.addAll(list)
        }

        // Sync Communication Data
        val userId = SupabaseRepository.getCurrentUserId()
        if (userId != null) {
            SupabaseRepository.fetchMessages(userId).onSuccess { list: List<Message> ->
                messages.clear()
                messages.addAll(list)
            }
        }
    }
}
