package com.ezDrop.app

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserId(id: Long) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1L)
        return if (id == -1L) null else id
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun saveSecondChanceLastOpen(userId: Long, time: Long) {
        prefs.edit().putLong("second_chance_last_open_$userId", time).apply()
    }

    fun getSecondChanceLastOpen(userId: Long): Long {
        return prefs.getLong("second_chance_last_open_$userId", 0L)
    }

    companion object {
        private const val PREFS_NAME = "ezdrop_session"
        private const val KEY_USER_ID = "user_id"
    }
}
