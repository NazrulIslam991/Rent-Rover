package com.example.rent_rover

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val IS_LOGGED_IN = "is_logged_in"
        const val USER_ID = "user_id"
    }

    fun createLoginSession(userId: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}
