package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Message
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class Success(val messages: List<Message>) : ChatState()
    data class Error(val message: String) : ChatState()
}

class ChatViewModel : ViewModel() {
    private val _chatState = mutableStateOf<ChatState>(ChatState.Idle)
    val chatState: State<ChatState> = _chatState

    fun loadMessages(otherId: String) {
        val userId = SupabaseRepository.getCurrentUserId() ?: return
        _chatState.value = ChatState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchChatMessages(userId, otherId)
            result.onSuccess {
                _chatState.value = ChatState.Success(it)
            }.onFailure {
                _chatState.value = ChatState.Error(it.message ?: "Failed to load messages")
            }
        }
    }

    fun sendMessage(receiverId: String, receiverName: String, text: String) {
        val userId = SupabaseRepository.getCurrentUserId() ?: return
        // Optimistic update could be added here
        viewModelScope.launch {
            val result = SupabaseRepository.sendMessage(
                senderId = userId,
                receiverId = receiverId,
                senderName = "Me", // Ideally fetch current user name
                messageText = text
            )
            result.onSuccess {
                loadMessages(receiverId) // Refresh after sending
            }
        }
    }
}
