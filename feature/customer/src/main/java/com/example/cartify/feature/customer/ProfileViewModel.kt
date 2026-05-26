package com.example.cartify.feature.customer

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.common.local.UserSession
import com.example.cartify.data.network.repository.SupabaseRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userSession = UserSession(application)

    private val _userName = mutableStateOf(userSession.getUserName())
    val userName: State<String> = _userName

    private val _userEmail = mutableStateOf(userSession.getUserEmail())
    val userEmail: State<String> = _userEmail

    private val _userRole = mutableStateOf(userSession.getRole())
    val userRole: State<String> = _userRole

    private val _isVendorMode = mutableStateOf(userSession.isVendorMode())
    val isVendorMode: State<Boolean> = _isVendorMode

    private val _profileImageUri = mutableStateOf(userSession.getProfileImage())
    val profileImageUri: State<String?> = _profileImageUri

    fun toggleVendorMode() {
        val newMode = !_isVendorMode.value
        userSession.setVendorMode(newMode)
        _isVendorMode.value = newMode
    }

    fun updateProfileImage(uri: String, imageBytes: ByteArray?) {
        _profileImageUri.value = uri
        userSession.saveProfileImage(uri)
        
        viewModelScope.launch {
            SupabaseRepository.updateProfile(_userName.value, imageBytes)
        }
    }

    fun refreshUserData() {
        _userName.value = userSession.getUserName()
        _userEmail.value = userSession.getUserEmail()
        _userRole.value = userSession.getRole()
        _isVendorMode.value = userSession.isVendorMode()
        _profileImageUri.value = userSession.getProfileImage()
    }

    fun logout() {
        userSession.logout()
    }
}
