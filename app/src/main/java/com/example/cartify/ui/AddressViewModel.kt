package com.example.cartify.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.model.Address
import com.example.cartify.data.repository.BackendRepository
import kotlinx.coroutines.launch

sealed class AddressState {
    object Idle : AddressState()
    object Loading : AddressState()
    data class Success(val addresses: List<Address>) : AddressState()
    data class Error(val message: String) : AddressState()
}

class AddressViewModel : ViewModel() {
    private val _addressState = mutableStateOf<AddressState>(AddressState.Idle)
    val addressState: State<AddressState> = _addressState

    private val _isSaving = mutableStateOf(false)
    val isSaving: State<Boolean> = _isSaving

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        _addressState.value = AddressState.Loading
        viewModelScope.launch {
            val result = BackendRepository.fetchAddresses()
            result.onSuccess {
                _addressState.value = AddressState.Success(it)
            }.onFailure {
                _addressState.value = AddressState.Error(it.message ?: "Failed to load addresses")
            }
        }
    }

    fun addAddress(title: String, fullAddress: String, isDefault: Boolean, onSuccess: () -> Unit) {
        _isSaving.value = true
        viewModelScope.launch {
            val result = BackendRepository.addAddress(title, fullAddress, isDefault)
            _isSaving.value = false
            result.onSuccess {
                loadAddresses() // Refresh the list
                onSuccess()
            }
        }
    }
}
