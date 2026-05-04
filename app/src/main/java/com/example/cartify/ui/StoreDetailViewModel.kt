package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Product
import com.example.cartify.data.model.Store
import com.example.cartify.data.repository.BackendRepository
import kotlinx.coroutines.launch

sealed class StoreDetailState {
    object Idle : StoreDetailState()
    object Loading : StoreDetailState()
    data class Success(val store: Store, val products: List<Product>) : StoreDetailState()
    data class Error(val message: String) : StoreDetailState()
}

class StoreDetailViewModel : ViewModel() {
    private val _storeDetailState = mutableStateOf<StoreDetailState>(StoreDetailState.Idle)
    val storeDetailState: State<StoreDetailState> = _storeDetailState

    fun loadStoreDetail(storeId: String) {
        _storeDetailState.value = StoreDetailState.Loading
        viewModelScope.launch {
            val result = BackendRepository.fetchStoreDetail(storeId)
            result.onSuccess { (store, products) ->
                _storeDetailState.value = StoreDetailState.Success(store, products)
            }.onFailure {
                _storeDetailState.value = StoreDetailState.Error(it.message ?: "Failed to load store details")
            }
        }
    }
}
