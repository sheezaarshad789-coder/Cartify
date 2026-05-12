package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Order
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class OrdersState {
    object Idle : OrdersState()
    object Loading : OrdersState()
    data class Success(val orders: List<Order>) : OrdersState()
    data class Error(val message: String) : OrdersState()
}

class OrdersViewModel : ViewModel() {
    private val _ordersState = mutableStateOf<OrdersState>(OrdersState.Idle)
    val ordersState: State<OrdersState> = _ordersState

    init {
        loadOrders()
    }

    fun loadOrders() {
        val userId = SupabaseRepository.getCurrentUserId()
        if (userId == null) {
            _ordersState.value = OrdersState.Error("User not logged in")
            return
        }

        _ordersState.value = OrdersState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchUserOrders(userId)
            result.onSuccess {
                _ordersState.value = OrdersState.Success(it)
            }.onFailure {
                _ordersState.value = OrdersState.Error(it.message ?: "Failed to load orders")
            }
        }
    }
}
