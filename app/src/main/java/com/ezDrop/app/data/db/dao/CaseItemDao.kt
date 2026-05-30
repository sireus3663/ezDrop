package com.ezDrop.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ezDrop.app.data.db.entity.CaseItemEntity

data class CaseItemWithDetails(
    val caseId: Long,
    val itemId: Long,
    val dropWeight: Int,
    val name: String,
    val rarity: String,
    val quality: String,
    val category: String,
    val imageRes: String
)

@Dao
interface CaseItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(caseItems: List<CaseItemEntity>): List<Long>

    @Query("""
        SELECT ci.caseId, ci.itemId, ci.dropWeight,
               i.name, i.rarity, i.quality, i.category, i.imageRes
        FROM case_items ci
        INNER JOIN items i ON ci.itemId = i.id
        WHERE ci.caseId = :caseId
        ORDER BY ci.dropWeight DESC
    """)
    suspend fun getItemsForCase(caseId: Long): List<CaseItemWithDetails>
}
