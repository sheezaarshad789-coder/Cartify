package com.example.cartify.feature.customer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.model.Category
import com.example.cartify.core.common.model.Product
import com.example.cartify.core.common.model.Store
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(
        val categories: List<Category>,
        val stores: List<Store>,
        val products: List<Product>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel : ViewModel() {
    private val _homeState = mutableStateOf<HomeState>(HomeState.Idle)
    val homeState: State<HomeState> = _homeState

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            try {
                val categoriesResult = SupabaseRepository.fetchCategories()
                val storesResult = SupabaseRepository.fetchStores()
                val productsResult = SupabaseRepository.fetchProducts()

                if (categoriesResult.isSuccess && storesResult.isSuccess && productsResult.isSuccess) {
                    _homeState.value = HomeState.Success(
                        categories = categoriesResult.getOrDefault(emptyList()),
                        stores = storesResult.getOrDefault(emptyList()),
                        products = productsResult.getOrDefault(emptyList())
                    )
                } else {
                    _homeState.value = HomeState.Error("Failed to load some data")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}
