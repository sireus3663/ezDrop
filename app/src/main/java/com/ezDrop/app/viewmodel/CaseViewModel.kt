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
import com.ezDrop.app.data.util.floatToPrice
import com.ezDrop.app.data.util.wearTier
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
    val wearFloat: Float = 0f,
    val wearTier: String = "",
    val finalPrice: Int = 0,
    val error: String? = null
) {
    val isSuccess get() = wonItem != null
}

data class CaseOpeningState(
    val isAnimating: Boolean = false,
    val showResults: Boolean = false,
    val results: List<CaseOpeningResult> = emptyList(),
    val count: Int = 1,
    val error: String? = null,
)

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

    private val _openingState = MutableStateFlow(CaseOpeningState())
    val openingState: StateFlow<CaseOpeningState> = _openingState.asStateFlow()

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
            _openingState.value = CaseOpeningState()
            val caseInfo = caseDao.getById(caseId) ?: return@launch
            val items = caseItemDao.getItemsForCase(caseId)
            val totalWeight = items.sumOf { it.dropWeight }
            _detail.value = CaseDetail(caseInfo, items, totalWeight)
        }
    }

    fun startOpening(count: Int) {
        val caseId = _caseId ?: return
        viewModelScope.launch {
            _openingState.value = CaseOpeningState(count = count, isAnimating = true)

            val detail = _detail.value ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch
            val user = userDao.getById(userId) ?: return@launch

            val totalPrice = detail.caseInfo.price * count

            if (user.level < detail.caseInfo.requiredLevel) {
                _openingState.value = CaseOpeningState(
                    count = count,
                    error = "Need level ${detail.caseInfo.requiredLevel}, you have ${user.level}"
                )
                return@launch
            }

            if (user.balance < totalPrice) {
                _openingState.value = CaseOpeningState(
                    count = count,
                    error = "Need $totalPrice$, you have ${user.balance}$"
                )
                return@launch
            }

            userDao.updateBalance(userId, user.balance - totalPrice)

            val results = List(count) { rollItem(detail) }

            for (r in results) {
                userDao.addxp(userId, r.finalPrice)
                inventoryDao.insert(
                    InventoryEntity(
                        userId = userId,
                        itemId = r.wonItem!!.itemId,
                        wearFloat = r.wearFloat,
                        finalPrice = r.finalPrice
                    )
                )
            }

            _openingState.value = CaseOpeningState(
                count = count,
                isAnimating = true,
                results = results,
            )
        }
    }

    fun onAnimationEnd() {
        val current = _openingState.value
        _openingState.value = current.copy(isAnimating = false, showResults = true)
    }

    fun resetOpeningState() {
        _openingState.value = CaseOpeningState()
    }

    private fun rollItem(detail: CaseDetail): CaseOpeningResult {
        val roll = Random.nextFloat() * detail.totalWeight
        var cumulative = 0f
        val wonItem = detail.items.first { item ->
            cumulative += item.dropWeight
            roll <= cumulative
        }
        val wearFloat = Random.nextFloat()
        val tier = wearTier(wearFloat)
        val finalPrice = floatToPrice(wonItem.basePrice, wearFloat)
        return CaseOpeningResult(
            wonItem = wonItem,
            wearFloat = wearFloat,
            wearTier = tier,
            finalPrice = finalPrice
        )
    }

    private var _caseId: Long? = null
}
