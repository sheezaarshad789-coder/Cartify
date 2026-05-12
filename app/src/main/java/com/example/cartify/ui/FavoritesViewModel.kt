package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Product
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Idle : FavoritesState()
    object Loading : FavoritesState()
    data class Success(val products: List<Product>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}

class FavoritesViewModel : ViewModel() {
    private val _favoritesState = mutableStateOf<FavoritesState>(FavoritesState.Idle)
    val favoritesState: State<FavoritesState> = _favoritesState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoritesState.value = FavoritesState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchFavoriteProducts()
            result.onSuccess {
                _favoritesState.value = FavoritesState.Success(it)
            }.onFailure {
                _favoritesState.value = FavoritesState.Error(it.message ?: "Failed to load favorites")
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            SupabaseRepository.toggleFavorite(product.id, product.isFavorite)
            loadFavorites() // Refresh list after toggle
        }
    }
}
