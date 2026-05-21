package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Notification
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class NotificationsState {
    object Idle : NotificationsState()
    object Loading : NotificationsState()
    data class Success(val notifications: List<Notification>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

class NotificationsViewModel : ViewModel() {
    private val _notificationsState = mutableStateOf<NotificationsState>(NotificationsState.Idle)
    val notificationsState: State<NotificationsState> = _notificationsState

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        _notificationsState.value = NotificationsState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchNotifications()
            result.onSuccess {
                _notificationsState.value = NotificationsState.Success(it)
            }.onFailure {
                _notificationsState.value = NotificationsState.Error(it.message ?: "Failed to load notifications")
            }
        }
    }
}
