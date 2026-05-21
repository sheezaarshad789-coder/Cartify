package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cartify.core.common.model.CartItem
import com.example.cartify.core.common.model.Product
import com.example.cartify.data.network.repository.FakeData

sealed class CartState {
    object Loading : CartState()
    data class Success(val items: List<CartItem>) : CartState()
    data class Error(val message: String) : CartState()
}

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems
    
    private val _cartState = mutableStateOf<CartState>(CartState.Success(emptyList()))
    val cartState: State<CartState> = _cartState

    private val _totalPrice = mutableStateOf(0.0)
    val totalPrice: State<Double> = _totalPrice

    init {
        _cartItems.addAll(FakeData.cartItems)
        updateState()
    }

    fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
        updateState()
    }

    fun removeFromCart(cartItem: CartItem) {
        val index = _cartItems.indexOf(cartItem)
        if (index != -1) {
            if (cartItem.quantity > 1) {
                _cartItems[index] = cartItem.copy(quantity = cartItem.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
        updateState()
    }

    fun clearCart() {
        _cartItems.clear()
        updateState()
    }

    private fun updateState() {
        _cartState.value = CartState.Success(_cartItems.toList())
        _totalPrice.value = _cartItems.sumOf { it.product.price * it.quantity }
    }
}
