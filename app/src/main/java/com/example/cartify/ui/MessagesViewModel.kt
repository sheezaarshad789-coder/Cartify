package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Message
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class MessagesState {
    object Idle : MessagesState()
    object Loading : MessagesState()
    data class Success(val messages: List<Message>) : MessagesState()
    data class Error(val message: String) : MessagesState()
}

class MessagesViewModel : ViewModel() {
    private val _messagesState = mutableStateOf<MessagesState>(MessagesState.Idle)
    val messagesState: State<MessagesState> = _messagesState

    init {
        loadMessages()
    }

    fun loadMessages() {
        val userId = SupabaseRepository.getCurrentUserId()
        if (userId == null) {
            _messagesState.value = MessagesState.Error("User not logged in")
            return
        }

        _messagesState.value = MessagesState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchMessages(userId)
            result.onSuccess {
                _messagesState.value = MessagesState.Success(it)
            }.onFailure {
                _messagesState.value = MessagesState.Error(it.message ?: "Failed to load messages")
            }
        }
    }
}
