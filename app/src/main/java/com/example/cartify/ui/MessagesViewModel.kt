package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Message
import com.example.cartify.data.repository.BackendRepository
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
        _messagesState.value = MessagesState.Loading
        viewModelScope.launch {
            val result = BackendRepository.fetchMessages()
            result.onSuccess {
                _messagesState.value = MessagesState.Success(it)
            }.onFailure {
                _messagesState.value = MessagesState.Error(it.message ?: "Failed to load messages")
            }
        }
    }
}
