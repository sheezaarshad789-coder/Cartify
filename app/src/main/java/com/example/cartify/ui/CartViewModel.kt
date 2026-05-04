package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cartify.data.model.CartItem
import com.example.cartify.data.model.Product
import com.example.cartify.data.repository.FakeData

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    private val _totalPrice = mutableStateOf(0.0)
    val totalPrice: State<Double> = _totalPrice

    init {
        // Initialize with fake data for now, or keep empty if starting fresh
        _cartItems.addAll(FakeData.cartItems)
        calculateTotal()
    }

    fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
        calculateTotal()
    }

    fun removeFromCart(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            val index = _cartItems.indexOf(cartItem)
            _cartItems[index] = cartItem.copy(quantity = cartItem.quantity - 1)
        } else {
            _cartItems.remove(cartItem)
        }
        calculateTotal()
    }

    fun clearCart() {
        _cartItems.clear()
        calculateTotal()
    }

    private fun calculateTotal() {
        _totalPrice.value = _cartItems.sumOf { it.product.price * it.quantity }
    }
}
