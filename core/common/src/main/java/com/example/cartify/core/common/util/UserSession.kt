package com.example.cartify.core.common.util

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(id: String, name: String, email: String, token: String, role: String) {
        prefs.edit().apply {
            putString("user_id", id)
            putString("user_name", name)
            putString("user_email", email)
            putString("auth_token", token)
            putString("user_role", role)
            apply()
        }
    }

    // New flag to handle onboarding visibility
    fun isOnboardingSeen(): Boolean = prefs.getBoolean("onboarding_seen", false)
    fun setOnboardingSeen() = prefs.edit().putBoolean("onboarding_seen", true).apply()

    fun setVendorMode(isVendor: Boolean) {
        prefs.edit().putBoolean("is_vendor_mode", isVendor).apply()
    }

    fun isVendorMode(): Boolean = prefs.getBoolean("is_vendor_mode", false)

    fun getUserId(): String? = prefs.getString("user_id", null)
    fun getUserName(): String = prefs.getString("user_name", "Guest") ?: "Guest"
    fun getUserEmail(): String = prefs.getString("user_email", "guest@example.com") ?: "guest@example.com"
    fun getToken(): String? = prefs.getString("auth_token", null)
    fun getRole(): String = prefs.getString("user_role", "user") ?: "user"

    fun logout() {
        prefs.edit().clear().apply()
    }
}
