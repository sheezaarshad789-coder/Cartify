package com.example.cartify.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.cartify.data.local.UserSession

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userSession = UserSession(application)

    private val _userName = mutableStateOf(userSession.getUserName())
    val userName: State<String> = _userName

    private val _userEmail = mutableStateOf(userSession.getUserEmail())
    val userEmail: State<String> = _userEmail

    fun refreshUserData() {
        _userName.value = userSession.getUserName()
        _userEmail.value = userSession.getUserEmail()
    }

    fun logout() {
        userSession.logout()
    }
}
