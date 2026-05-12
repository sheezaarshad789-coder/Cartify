package com.example.cartify.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.data.local.UserSession
import com.example.cartify.data.remote.SupabaseManager
import com.example.cartify.data.repository.SupabaseRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState
    
    private val userSession = UserSession(application)

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.login(email, password)
            result.onSuccess {
                // Fetch user data from session
                val user = SupabaseManager.client.auth.currentUserOrNull()
                val role = user?.userMetadata?.get("role")?.toString()?.replace("\"", "") ?: "user"
                val name = user?.userMetadata?.get("name")?.toString()?.replace("\"", "") ?: "User"
                
                userSession.saveUser(
                    id = user?.id ?: "",
                    name = name,
                    email = user?.email ?: email,
                    token = SupabaseManager.client.auth.currentAccessTokenOrNull() ?: "",
                    role = role
                )
                // Set initial mode based on role
                userSession.setVendorMode(role == "vendor")
                
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, password: String, role: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = SupabaseRepository.signup(name, email, password, role)
            result.onSuccess {
                val user = SupabaseManager.client.auth.currentUserOrNull()
                userSession.saveUser(
                    id = user?.id ?: "",
                    name = name,
                    email = email,
                    token = SupabaseManager.client.auth.currentAccessTokenOrNull() ?: "",
                    role = role
                )
                userSession.setVendorMode(role == "vendor")
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Signup failed")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
