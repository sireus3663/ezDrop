package com.ezDrop.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezDrop.app.EzDropApp
import com.ezDrop.app.SessionManager
import com.ezDrop.app.data.db.dao.CaseItemWithDetails
import com.ezDrop.app.data.db.entity.CaseEntity
import com.ezDrop.app.data.db.entity.InventoryEntity
import com.ezDrop.app.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CaseDetail(
    val caseInfo: CaseEntity,
    val items: List<CaseItemWithDetails>,
    val totalWeight: Int
)

data class CaseOpeningResult(
    val wonItem: CaseItemWithDetails? = null,
    val error: String? = null
) {
    val isSuccess get() = wonItem != null
}

class CaseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as EzDropApp).database
    private val caseDao = database.caseDao()
    private val caseItemDao = database.caseItemDao()
    private val userDao = database.userDao()
    private val inventoryDao = database.inventoryDao()
    private val inventoryRepository = InventoryRepository(
        database.itemDao(), database.inventoryDao()
    )
    private val sessionManager = SessionManager(application)

    private val _cases = MutableStateFlow<List<CaseEntity>>(emptyList())
    val cases: StateFlow<List<CaseEntity>> = _cases.asStateFlow()

    private val _detail = MutableStateFlow<CaseDetail?>(null)
    val detail: StateFlow<CaseDetail?> = _detail.asStateFlow()

    private val _openingResult = MutableStateFlow<CaseOpeningResult?>(null)
    val openingResult: StateFlow<CaseOpeningResult?> = _openingResult.asStateFlow()

    init {
        loadCases()
    }

    fun loadCases() {
        viewModelScope.launch {
            _cases.value = caseDao.getAll()
        }
    }

    fun loadCaseDetail(caseId: Long) {
        viewModelScope.launch {
            _caseId = caseId
            _openingResult.value = null
            val caseInfo = caseDao.getById(caseId) ?: return@launch
            val items = caseItemDao.getItemsForCase(caseId)
            val totalWeight = items.sumOf { it.dropWeight }
            _detail.value = CaseDetail(caseInfo, items, totalWeight)
        }
    }

    fun resetOpeningResult() {
        _openingResult.value = null
    }

    fun openCase() {
        val caseId = _caseId ?: return
        viewModelScope.launch {
            val detail = _detail.value ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch
            val user = userDao.getById(userId) ?: return@launch

            if (user.level < detail.caseInfo.requiredLevel) {
                _openingResult.value = CaseOpeningResult(
                    error = "Need level ${detail.caseInfo.requiredLevel}, you have ${user.level}"
                )
                return@launch
            }

            if (user.balance < detail.caseInfo.price) {
                _openingResult.value = CaseOpeningResult(
                    error = "Need ${detail.caseInfo.price}$, you have ${user.balance}$"
                )
                return@launch
            }

            userDao.updateBalance(userId, user.balance - detail.caseInfo.price)

            val roll = Random.nextFloat() * detail.totalWeight
            var cumulative = 0f
            val wonItem = detail.items.first { item ->
                cumulative += item.dropWeight
                roll <= cumulative
            }

            inventoryDao.insert(
                InventoryEntity(userId = userId, itemId = wonItem.itemId)
            )

            _openingResult.value = CaseOpeningResult(wonItem = wonItem)
        }
    }

    private var _caseId: Long? = null
}
