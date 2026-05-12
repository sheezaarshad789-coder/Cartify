package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Faq
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class HelpCenterState {
    object Idle : HelpCenterState()
    object Loading : HelpCenterState()
    data class Success(val faqs: List<Faq>) : HelpCenterState()
    data class Error(val message: String) : HelpCenterState()
}

class HelpCenterViewModel : ViewModel() {
    private val _helpCenterState = mutableStateOf<HelpCenterState>(HelpCenterState.Idle)
    val helpCenterState: State<HelpCenterState> = _helpCenterState

    init {
        loadFaqs()
    }

    fun loadFaqs() {
        _helpCenterState.value = HelpCenterState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchFaqs()
            result.onSuccess {
                _helpCenterState.value = HelpCenterState.Success(it)
            }.onFailure {
                _helpCenterState.value = HelpCenterState.Error(it.message ?: "Failed to load FAQs")
            }
        }
    }
}
