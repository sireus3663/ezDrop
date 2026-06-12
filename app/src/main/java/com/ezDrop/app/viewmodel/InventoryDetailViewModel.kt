package com.ezDrop.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezDrop.app.EzDropApp
import com.ezDrop.app.SessionManager
import com.ezDrop.app.data.db.dao.InventoryItemEntry
import com.ezDrop.app.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryDetailState(
    val entry: InventoryItemEntry? = null,
    val isLoading: Boolean = true,
    val isSelling: Boolean = false,
    val sold: Boolean = false,
    val error: String? = null,
)

class InventoryDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as EzDropApp).database
    private val userDao = database.userDao()
    private val inventoryRepository = InventoryRepository(
        database.itemDao(), database.inventoryDao()
    )
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(InventoryDetailState())
    val state: StateFlow<InventoryDetailState> = _state.asStateFlow()

    fun loadEntry(inventoryId: Long) {
        viewModelScope.launch {
            _state.value = InventoryDetailState(isLoading = true)
            val entry = inventoryRepository.getInventoryEntry(inventoryId)
            _state.value = InventoryDetailState(entry = entry, isLoading = false)
        }
    }

    fun sellItem() {
        val entry = _state.value.entry ?: return
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSelling = true)
            inventoryRepository.deleteInventoryItem(entry.inventoryId)
            val user = userDao.getById(userId) ?: return@launch
            userDao.updateBalance(userId, user.balance + entry.finalPrice)
            val newNetWorth = user.netWorth + entry.finalPrice
            userDao.updateNetWorth(userId, newNetWorth)
            _state.value = _state.value.copy(isSelling = false, sold = true)
        }
    }
}
