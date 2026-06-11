package com.ezDrop.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezDrop.app.EzDropApp
import com.ezDrop.app.SessionManager
import com.ezDrop.app.data.db.entity.UserEntity
import com.ezDrop.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainState(
    val user: UserEntity? = null,
    val nickname: String = "",
    val balance: Int = 0,
    val level: Int = 1,
    val isLoading: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(
        (application as EzDropApp).database.userDao()
    )
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            val user = authRepository.getUser(userId)
            if (user != null) {
                _state.value = MainState(
                    user = user,
                    nickname = user.nickname,
                    balance = user.balance,
                    level = user.level,
                    isLoading = false
                )
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun logout() {
        sessionManager.clear()
        _state.value = MainState()
    }

    fun refreshUser() {
        loadUser()
    }
}
