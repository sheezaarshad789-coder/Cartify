package com.example.cartify.data.local

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String, token: String) {
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            putString("auth_token", token)
            apply()
        }
    }

    fun getUserName(): String = prefs.getString("user_name", "Guest") ?: "Guest"
    fun getUserEmail(): String = prefs.getString("user_email", "guest@example.com") ?: "guest@example.com"
    fun getToken(): String? = prefs.getString("auth_token", null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
