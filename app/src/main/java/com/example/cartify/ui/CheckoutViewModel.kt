package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.CartItem
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    object Success : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class CheckoutViewModel : ViewModel() {
    private val _checkoutState = mutableStateOf<CheckoutState>(CheckoutState.Idle)
    val checkoutState: State<CheckoutState> = _checkoutState

    fun placeOrder(cartItems: List<CartItem>, totalAmount: Double, userId: String, onSuccess: () -> Unit) {
        _checkoutState.value = CheckoutState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.placeOrder(cartItems, totalAmount, userId)
            result.onSuccess {
                _checkoutState.value = CheckoutState.Success
                onSuccess()
            }.onFailure {
                _checkoutState.value = CheckoutState.Error(it.message ?: "Failed to place order")
            }
        }
    }
}
