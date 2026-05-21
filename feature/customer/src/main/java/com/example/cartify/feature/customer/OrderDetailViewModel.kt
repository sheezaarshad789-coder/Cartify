package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Order
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class OrderDetailState {
    object Idle : OrderDetailState()
    object Loading : OrderDetailState()
    data class Success(val order: Order) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}

class OrderDetailViewModel : ViewModel() {
    private val _orderState = mutableStateOf<OrderDetailState>(OrderDetailState.Idle)
    val orderState: State<OrderDetailState> = _orderState

    fun loadOrderDetail(orderId: String) {
        _orderState.value = OrderDetailState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchOrderById(orderId)
            result.onSuccess {
                _orderState.value = OrderDetailState.Success(it)
            }.onFailure {
                _orderState.value = OrderDetailState.Error(it.message ?: "Failed to load order details")
            }
        }
    }
}
