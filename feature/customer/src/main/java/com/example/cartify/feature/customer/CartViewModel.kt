package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Product
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class Success(val items: List<CartItem>) : CartState()
    data class Error(val message: String) : CartState()
}

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems
    
    private val _cartState = mutableStateOf<CartState>(CartState.Idle)
    val cartState: State<CartState> = _cartState

    private val _totalPrice = mutableStateOf(0.0)
    val totalPrice: State<Double> = _totalPrice

    init {
        loadCart()
    }

    fun loadCart() {
        _cartState.value = CartState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchCartItems()
            result.onSuccess { items ->
                _cartItems.clear()
                _cartItems.addAll(items)
                updateState()
            }.onFailure {
                _cartState.value = CartState.Error(it.message ?: "Failed to load cart")
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val result = SupabaseRepository.addToCart(product.id, 1)
            if (result.isSuccess) {
                loadCart() // Refresh from DB to keep UI in sync
            }
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            // repository deletes the row for this product_id
            val result = SupabaseRepository.removeFromCart(cartItem.product.id)
            if (result.isSuccess) {
                loadCart()
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val result = SupabaseRepository.clearCart()
            if (result.isSuccess) {
                _cartItems.clear()
                updateState()
            }
        }
    }

    private fun updateState() {
        _cartState.value = CartState.Success(_cartItems.toList())
        _totalPrice.value = _cartItems.sumOf { it.product.price * it.quantity }
    }
}
