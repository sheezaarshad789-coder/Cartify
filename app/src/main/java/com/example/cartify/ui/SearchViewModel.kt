package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Product
import com.example.cartify.data.repository.BackendRepository
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val products: List<Product>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel : ViewModel() {
    private val _searchState = mutableStateOf<SearchState>(SearchState.Idle)
    val searchState: State<SearchState> = _searchState

    fun search(query: String) {
        if (query.isBlank()) return
        
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            val result = BackendRepository.searchProducts(query)
            result.onSuccess {
                _searchState.value = SearchState.Success(it)
            }.onFailure {
                _searchState.value = SearchState.Error(it.message ?: "Search failed")
            }
        }
    }
    
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            BackendRepository.toggleFavorite(product.id)
            // Ideally, we'd update the local state here as well
        }
    }
}
