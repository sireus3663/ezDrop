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

data class InventoryState(
    val items: List<InventoryItemEntry> = emptyList(),
    val isLoading: Boolean = false,
    val sellingInventoryId: Long? = null,
    val error: String? = null,
    val balanceVersion: Int = 0
)

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as EzDropApp).database
    private val userDao = database.userDao()
    private val inventoryRepository = InventoryRepository(
        database.itemDao(), database.inventoryDao()
    )
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    fun loadInventory() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val items = inventoryRepository.getUserInventoryEntries(userId)
            _state.value = _state.value.copy(items = items, isLoading = false)
        }
    }

    fun sellItem(entry: InventoryItemEntry) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(sellingInventoryId = entry.inventoryId)

            inventoryRepository.deleteInventoryItem(entry.inventoryId)

            val user = userDao.getById(userId) ?: return@launch
            userDao.updateBalance(userId, user.balance + entry.finalPrice)
            val newNetWorth = user.netWorth + entry.finalPrice
            userDao.updateNetWorth(userId, newNetWorth)

            val items = inventoryRepository.getUserInventoryEntries(userId)
            _state.value = _state.value.copy(
                items = items,
                sellingInventoryId = null,
                balanceVersion = _state.value.balanceVersion + 1
            )
        }
    }
}
