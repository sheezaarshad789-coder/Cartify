package com.example.cartify.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.mutableStateListOf
import com.example.cartify.data.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object FakeData {
    val categories = mutableStateListOf(
        Category("1", "Fruits", Icons.Default.ShoppingCart),
        Category("2", "Vegetables", Icons.Default.Menu),
        Category("3", "Dairy", Icons.Default.Coffee),
        Category("4", "Bakery", Icons.Default.BakeryDining),
        Category("5", "Meat", Icons.Default.Restaurant),
        Category("6", "Snacks", Icons.Default.Fastfood)
    )

    val stores = mutableStateListOf(
        Store("1", "Fresh Mart", 4.5, "1.2 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200"),
        Store("2", "Grocery Hub", 4.2, "2.5 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200"),
        Store("3", "Organic Store", 4.8, "3.0 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200"),
        Store("4", "Daily Needs", 4.0, "1.0 km", "https://via.placeholder.com/150", "https://via.placeholder.com/400x200")
    )

    val products = mutableStateListOf(
        Product("1", "Avocado", 4.0, "20g", "https://via.placeholder.com/150", "Fresh Avocado", "1", "Fresh Mart", "1"),
        Product("2", "Yam", 25.0, "1kg", "https://via.placeholder.com/150", "Sweet Yam", "2", "Grocery Hub", "2"),
        Product("3", "Eggs (Crate)", 40.0, "900g", "https://via.placeholder.com/150", "Farm Fresh Eggs", "1", "Fresh Mart", "3"),
        Product("4", "Fresh Tomatoes", 80.0, "1kg", "https://via.placeholder.com/150", "Red Tomatoes", "3", "Organic Store", "2"),
        Product("5", "Organic Milk", 15.0, "1L", "https://via.placeholder.com/150", "Fresh Cow Milk", "3", "Organic Store", "3"),
        Product("6", "Whole Wheat Bread", 12.0, "500g", "https://via.placeholder.com/150", "Bakery Bread", "2", "Grocery Hub", "4"),
        Product("7", "Red Apple", 10.0, "1kg", "https://via.placeholder.com/150", "Crispy Apples", "1", "Fresh Mart", "1"),
        Product("8", "Chicken Breast", 45.0, "1kg", "https://via.placeholder.com/150", "Fresh Chicken", "4", "Daily Needs", "5")
    )

    val cartItems = mutableStateListOf(
        CartItem(products[0], 2), // Avocado from Fresh Mart
        CartItem(products[1], 1), // Yam from Grocery Hub
        CartItem(products[2], 2)  // Eggs from Fresh Mart
    )

    val orders = mutableStateListOf(
        Order("ORD123", "Fresh Mart", "Delivered", "20 July 2025", 8.0, listOf(cartItems[0])),
        Order("ORD456", "Grocery Hub", "Delivered", "20 July 2025", 25.0, listOf(cartItems[1])),
        Order("ORD789", "Fresh Mart", "Delivered", "20 July 2025", 80.0, listOf(cartItems[2]))
    )

    val messages = mutableStateListOf(
        Message("1", "Fresh Mart", "Your order is on the way!", "10:30 AM"),
        Message("2", "Grocery Hub", "We have received your order.", "Yesterday"),
        Message("3", "Organic Store", "Hello, how can I help you?", "2 days ago")
    )

    val addresses = mutableStateListOf(
        Address("1", "Home", "123 Green Valley, Eco City", true, Icons.Default.Home),
        Address("2", "Office", "456 Tech Park, Digital District", false, Icons.Default.Work)
    )

    suspend fun syncFromBackend() = coroutineScope {
        val categoriesDeferred = async { BackendRepository.fetchCategories() }
        val storesDeferred = async { BackendRepository.fetchStores() }
        val productsDeferred = async { BackendRepository.fetchProducts() }
        val ordersDeferred = async { BackendRepository.fetchOrders() }
        val messagesDeferred = async { BackendRepository.fetchMessages() }
        val addressesDeferred = async { BackendRepository.fetchAddresses() }

        categoriesDeferred.await().onSuccess {
            if (it.isNotEmpty()) {
                categories.clear()
                categories.addAll(it)
            }
        }
        storesDeferred.await().onSuccess {
            if (it.isNotEmpty()) {
                stores.clear()
                stores.addAll(it)
            }
        }
        productsDeferred.await().onSuccess {
            if (it.isNotEmpty()) {
                products.clear()
                products.addAll(it)
            }
        }
        ordersDeferred.await().onSuccess {
            orders.clear()
            orders.addAll(it)
        }
        messagesDeferred.await().onSuccess {
            if (it.isNotEmpty()) {
                messages.clear()
                messages.addAll(it)
            }
        }
        addressesDeferred.await().onSuccess {
            if (it.isNotEmpty()) {
                addresses.clear()
                addresses.addAll(it)
            }
        }
    }
}
