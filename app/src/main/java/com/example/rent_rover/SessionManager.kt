package com.example.rent_rover

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = prefs.edit()

    // Keys for session data
    companion object {
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_ID = "user_id"
        const val USER_EMAIL = "user_email"
        const val USER_NAME = "user_name"
    }

    // Save user session data
    fun createLoginSession(userId: String, email: String, name: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(USER_ID, userId)
        editor.putString(USER_EMAIL, email)
        editor.putString(USER_NAME, name)
        editor.apply()
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    // Get stored session data
    fun getUserDetails(): Map<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails[USER_ID] = prefs.getString(USER_ID, "") ?: ""
        userDetails[USER_EMAIL] = prefs.getString(USER_EMAIL, "") ?: ""
        userDetails[USER_NAME] = prefs.getString(USER_NAME, "") ?: ""
        return userDetails
    }

    // Clear session data when user logs out
    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
