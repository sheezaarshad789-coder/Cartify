package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.OrderTracking
import com.example.cartify.data.repository.BackendRepository
import kotlinx.coroutines.launch

sealed class OrderTrackingState {
    object Idle : OrderTrackingState()
    object Loading : OrderTrackingState()
    data class Success(val tracking: OrderTracking) : OrderTrackingState()
    data class Error(val message: String) : OrderTrackingState()
}

class OrderTrackingViewModel : ViewModel() {
    private val _trackingState = mutableStateOf<OrderTrackingState>(OrderTrackingState.Idle)
    val trackingState: State<OrderTrackingState> = _trackingState

    fun loadOrderTracking(orderId: String) {
        _trackingState.value = OrderTrackingState.Loading
        viewModelScope.launch {
            val result = BackendRepository.fetchOrderTracking(orderId)
            result.onSuccess {
                _trackingState.value = OrderTrackingState.Success(it)
            }.onFailure {
                _trackingState.value = OrderTrackingState.Error(it.message ?: "Failed to load tracking info")
            }
        }
    }
}
