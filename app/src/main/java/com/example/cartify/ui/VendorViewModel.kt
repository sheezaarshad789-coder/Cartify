package com.example.cartify.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.local.UserSession
import com.example.cartify.data.model.Order
import com.example.cartify.data.model.Product
import com.example.cartify.data.model.Store
import com.example.cartify.data.remote.ProductDto
import com.example.cartify.data.remote.StoreDto
import com.example.cartify.data.remote.VendorStatsDto
import com.example.cartify.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class VendorDashboardState(
    val stats: VendorStatsDto = VendorStatsDto(0.0, 0, 0),
    val recentOrders: List<Order> = emptyList(),
    val products: List<Product> = emptyList(),
    val store: Store? = null,
    val isLoading: Boolean = false,
    val isOperationLoading: Boolean = false,
    val error: String? = null
)

class VendorViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = mutableStateOf(VendorDashboardState())
    val state: State<VendorDashboardState> = _state
    
    private val userSession = UserSession(application)
    private val storeId: String get() = userSession.getUserId() ?: ""

    init {
        refreshAll()
        observeOrders()
    }

    fun refreshAll() {
        if (storeId.isEmpty()) return
        loadDashboardData()
        loadProducts()
        loadStoreInfo()
    }

    private fun loadDashboardData() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val statsResult = SupabaseRepository.fetchVendorStats(storeId)
                val ordersResult = SupabaseRepository.fetchVendorOrders(storeId)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    stats = statsResult.getOrDefault(VendorStatsDto(0.0, 0, 0)),
                    recentOrders = ordersResult.getOrDefault(emptyList())
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun loadStoreInfo() {
        viewModelScope.launch {
            SupabaseRepository.fetchStoreById(storeId).onSuccess {
                _state.value = _state.value.copy(store = it)
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            val result = SupabaseRepository.fetchVendorProducts(storeId)
            result.onSuccess { vendorProducts ->
                _state.value = _state.value.copy(products = vendorProducts)
            }
        }
    }

    private fun observeOrders() {
        if (storeId.isEmpty()) return
        viewModelScope.launch {
            SupabaseRepository.observeVendorOrders(storeId).collectLatest { updatedOrders ->
                val statsResult = SupabaseRepository.fetchVendorStats(storeId)
                _state.value = _state.value.copy(
                    recentOrders = updatedOrders,
                    stats = statsResult.getOrDefault(_state.value.stats)
                )
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            SupabaseRepository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun addProduct(product: ProductDto, imageBytes: ByteArray?, onSuccess: () -> Unit) {
        _state.value = _state.value.copy(isOperationLoading = true)
        viewModelScope.launch {
            val result = SupabaseRepository.addProduct(product, imageBytes)
            _state.value = _state.value.copy(isOperationLoading = false)
            if (result.isSuccess) {
                refreshAll()
                onSuccess()
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun updateProduct(product: ProductDto, imageBytes: ByteArray?, onSuccess: () -> Unit) {
        _state.value = _state.value.copy(isOperationLoading = true)
        viewModelScope.launch {
            val result = SupabaseRepository.updateProduct(product, imageBytes)
            _state.value = _state.value.copy(isOperationLoading = false)
            if (result.isSuccess) {
                refreshAll()
                onSuccess()
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun updateStore(storeDto: StoreDto, logoBytes: ByteArray?, bannerBytes: ByteArray?, onSuccess: () -> Unit) {
        _state.value = _state.value.copy(isOperationLoading = true)
        viewModelScope.launch {
            val result = SupabaseRepository.updateStore(storeDto, logoBytes, bannerBytes)
            _state.value = _state.value.copy(isOperationLoading = false)
            if (result.isSuccess) {
                loadStoreInfo()
                onSuccess()
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun toggleProductAvailability(productId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            val result = SupabaseRepository.toggleProductAvailability(productId, isAvailable)
            if (result.isSuccess) {
                val updatedProducts = _state.value.products.map {
                    if (it.id == productId) it.copy(isAvailable = isAvailable) else it
                }
                _state.value = _state.value.copy(products = updatedProducts)
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = SupabaseRepository.deleteProduct(productId)
            if (result.isSuccess) {
                loadProducts()
                val statsResult = SupabaseRepository.fetchVendorStats(storeId)
                _state.value = _state.value.copy(stats = statsResult.getOrDefault(_state.value.stats))
            }
        }
    }
}
