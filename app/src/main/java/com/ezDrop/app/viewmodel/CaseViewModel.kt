package com.ezDrop.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezDrop.app.EzDropApp
import com.ezDrop.app.data.db.dao.CaseItemWithDetails
import com.ezDrop.app.data.db.entity.CaseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CaseDetail(
    val caseInfo: CaseEntity,
    val items: List<CaseItemWithDetails>,
    val totalWeight: Int
)

class CaseViewModel(application: Application) : AndroidViewModel(application) {
    private val caseDao = (application as EzDropApp).database.caseDao()
    private val caseItemDao = (application as EzDropApp).database.caseItemDao()

    private val _cases = MutableStateFlow<List<CaseEntity>>(emptyList())
    val cases: StateFlow<List<CaseEntity>> = _cases.asStateFlow()

    private val _detail = MutableStateFlow<CaseDetail?>(null)
    val detail: StateFlow<CaseDetail?> = _detail.asStateFlow()

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
            val caseInfo = caseDao.getById(caseId) ?: return@launch
            val items = caseItemDao.getItemsForCase(caseId)
            val totalWeight = items.sumOf { it.dropWeight }
            _detail.value = CaseDetail(caseInfo, items, totalWeight)
        }
    }
}
