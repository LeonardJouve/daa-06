package ch.heigvd.iict.and.rest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

class SessionManager(context: Context) {
    companion object {
        private const val KEY = "SESSION_ID"
    }

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveSession(sessionId: String) {
        prefs.edit {
            putString(KEY, sessionId)
        }
    }

    fun getSession(): String? {
        return prefs.getString(KEY, null)
    }
}