package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Product
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class ProductListingState {
    object Idle : ProductListingState()
    object Loading : ProductListingState()
    data class Success(val products: List<Product>) : ProductListingState()
    data class Error(val message: String) : ProductListingState()
}

class ProductListingViewModel : ViewModel() {
    private val _listingState = mutableStateOf<ProductListingState>(ProductListingState.Idle)
    val listingState: State<ProductListingState> = _listingState

    fun loadProductsByCategory(categoryId: String?) {
        _listingState.value = ProductListingState.Loading
        viewModelScope.launch {
            val result = if (categoryId == null || categoryId == "all") {
                SupabaseRepository.fetchProducts()
            } else {
                SupabaseRepository.fetchProductsByCategory(categoryId)
            }

            result.onSuccess {
                _listingState.value = ProductListingState.Success(it)
            }.onFailure {
                _listingState.value = ProductListingState.Error(it.message ?: "Failed to load products")
            }
        }
    }
}
