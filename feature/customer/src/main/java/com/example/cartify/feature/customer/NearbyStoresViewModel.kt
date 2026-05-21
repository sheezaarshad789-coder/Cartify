package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Store
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class NearbyStoresState {
    object Idle : NearbyStoresState()
    object Loading : NearbyStoresState()
    data class Success(val stores: List<Store>) : NearbyStoresState()
    data class Error(val message: String) : NearbyStoresState()
}

class NearbyStoresViewModel : ViewModel() {
    private val _storesState = mutableStateOf<NearbyStoresState>(NearbyStoresState.Idle)
    val storesState: State<NearbyStoresState> = _storesState

    init {
        loadStores()
    }

    fun loadStores() {
        _storesState.value = NearbyStoresState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchStores()
            result.onSuccess {
                _storesState.value = NearbyStoresState.Success(it)
            }.onFailure {
                _storesState.value = NearbyStoresState.Error(it.message ?: "Failed to load stores")
            }
        }
    }
}
