package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Product
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class ProductDetailState {
    object Idle : ProductDetailState()
    object Loading : ProductDetailState()
    data class Success(val product: Product) : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
}

class ProductDetailViewModel : ViewModel() {
    private val _productDetailState = mutableStateOf<ProductDetailState>(ProductDetailState.Idle)
    val productDetailState: State<ProductDetailState> = _productDetailState

    fun loadProductDetail(productId: String) {
        _productDetailState.value = ProductDetailState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.fetchProductById(productId)
            result.onSuccess {
                _productDetailState.value = ProductDetailState.Success(it)
            }.onFailure {
                _productDetailState.value = ProductDetailState.Error(it.message ?: "Failed to load product details")
            }
        }
    }
}
