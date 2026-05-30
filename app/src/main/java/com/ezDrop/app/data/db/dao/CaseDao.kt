package com.ezDrop.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ezDrop.app.data.db.entity.CaseEntity

@Dao
interface CaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CaseEntity>): List<Long>

    @Query("SELECT * FROM cases ORDER BY price ASC")
    suspend fun getAll(): List<CaseEntity>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getById(id: Long): CaseEntity?
}
