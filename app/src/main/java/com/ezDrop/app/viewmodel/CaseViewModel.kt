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
import kotlinx.coroutines.flow.collectLatest
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
        viewModelScope.launch {
            val uid = sessionManager.getUserId() ?: return@launch
            userDao.getByIdFlow(uid).collectLatest {
                loadCases()
            }
        }
    }

    fun loadCases() {
        viewModelScope.launch {
            val all = caseDao.getAll()
            val userId = sessionManager.getUserId()
            if (userId != null) {
                val user = userDao.getById(userId)
                if (user != null) {
                    val invValue = inventoryDao.getInventoryValue(userId)
                    val cheapestPrice = all.filter { it.name != "Second Chance" }.minOfOrNull { it.price } ?: Int.MAX_VALUE
                    _cases.value = if (user.balance + invValue < cheapestPrice) all else all.filter { it.name != "Second Chance" }
                    return@launch
                }
            }
            _cases.value = all
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
            val actualCount = if (detail.value?.caseInfo?.price == 0) 1 else count
            _openingState.value = CaseOpeningState(count = actualCount, isAnimating = true)

            val detail = _detail.value ?: return@launch
            val userId = sessionManager.getUserId() ?: return@launch
            val user = userDao.getById(userId) ?: return@launch

            val totalPrice = detail.caseInfo.price * actualCount

            if (user.level < detail.caseInfo.requiredLevel) {
                _openingState.value = CaseOpeningState(
                    count = actualCount,
                    error = "Need level ${detail.caseInfo.requiredLevel}, you have ${user.level}"
                )
                return@launch
            }

            if (detail.caseInfo.price > 0) {
                if (user.balance < totalPrice) {
                    _openingState.value = CaseOpeningState(
                        count = actualCount,
                        error = "Need $totalPrice$, you have ${user.balance}$"
                    )
                    return@launch
                }
                userDao.updateBalance(userId, user.balance - totalPrice)
            } else {
                val now = System.currentTimeMillis()
                val lastOpen = sessionManager.getSecondChanceLastOpen(userId)
                if (lastOpen > 0 && now - lastOpen < 3_600_000) {
                    val remaining = (3_600_000 - (now - lastOpen)) / 1000
                    _openingState.value = CaseOpeningState(
                        count = actualCount,
                        error = "Second Chance available once per hour. Try again in ${remaining / 60}:${"%02d".format(remaining % 60)}."
                    )
                    return@launch
                }
                sessionManager.saveSecondChanceLastOpen(userId, now)
            }

            val results = List(actualCount) { rollItem(detail) }

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

            if (detail.caseInfo.price == 0) {
                loadCases()
            }

            _openingState.value = CaseOpeningState(
                count = actualCount,
                isAnimating = true,
                results = results,
            )
        }
    }

    fun onAnimationEnd() {
        val current = _openingState.value
        _openingState.value = current.copy(showResults = true)
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
