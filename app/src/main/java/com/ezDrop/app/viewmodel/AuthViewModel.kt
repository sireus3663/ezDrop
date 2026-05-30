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

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(
        (application as EzDropApp).database.userDao()
    )
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        val userId = sessionManager.getUserId()
        if (userId != null) {
            viewModelScope.launch {
                val user = repository.getUser(userId)
                if (user != null) {
                    _state.value = AuthState(isLoggedIn = true, user = user)
                } else {
                    sessionManager.clear()
                }
            }
        }
    }

    fun register(
        nickname: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (nickname.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Fill in all fields")
            return
        }
        if (password != confirmPassword) {
            _state.value = _state.value.copy(error = "Passwords don't match")
            return
        }
        if (password.length < 6) {
            _state.value =
                _state.value.copy(error = "Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.register(nickname, email, password)
            result.fold(
                onSuccess = { user ->
                    sessionManager.saveUserId(user.id)
                    _state.value = AuthState(isLoggedIn = true, user = user)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Fill in all fields")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    sessionManager.saveUserId(user.id)
                    _state.value = AuthState(isLoggedIn = true, user = user)
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun logout() {
        sessionManager.clear()
        _state.value = AuthState()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
