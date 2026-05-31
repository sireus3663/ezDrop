package com.ezDrop.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ezDrop.app.data.db.entity.InventoryEntity
import com.ezDrop.app.data.db.entity.ItemEntity

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(inventory: InventoryEntity): Long

    @Query("""
        SELECT items.* FROM items
        INNER JOIN user_inventory ON items.id = user_inventory.itemId
        WHERE user_inventory.userId = :userId
    """)
    suspend fun getUserItems(userId: Long): List<ItemEntity>

    @Query("DELETE FROM user_inventory WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("""
        SELECT COALESCE(SUM(items.price), 0) FROM items
        INNER JOIN user_inventory ON items.id = user_inventory.itemId
        WHERE user_inventory.userId = :userId
    """)
    suspend fun getInventoryValue(userId: Long): Int
}
