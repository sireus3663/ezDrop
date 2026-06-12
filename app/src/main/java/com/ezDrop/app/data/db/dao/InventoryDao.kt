package com.ezDrop.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ezDrop.app.data.db.entity.InventoryEntity
import com.ezDrop.app.data.db.entity.ItemEntity

data class InventoryItemEntry(
    val inventoryId: Long,
    val itemId: Long,
    val name: String,
    val rarity: String,
    val category: String,
    val imageRes: String,
    val basePrice: Int,
    val wearFloat: Float,
    val finalPrice: Int
)

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
        SELECT COALESCE(SUM(items.basePrice), 0) FROM items
        INNER JOIN user_inventory ON items.id = user_inventory.itemId
        WHERE user_inventory.userId = :userId
    """)
    suspend fun getInventoryValue(userId: Long): Int

    @Query("""
        SELECT ui.id AS inventoryId, i.id AS itemId, i.name, i.rarity,
               i.category, i.imageRes, i.basePrice,
               ui.wearFloat, ui.finalPrice
        FROM user_inventory ui
        INNER JOIN items i ON ui.itemId = i.id
        WHERE ui.userId = :userId
    """)
    suspend fun getUserInventoryEntries(userId: Long): List<InventoryItemEntry>

    @Query("""
        SELECT ui.id AS inventoryId, i.id AS itemId, i.name, i.rarity,
               i.category, i.imageRes, i.basePrice,
               ui.wearFloat, ui.finalPrice
        FROM user_inventory ui
        INNER JOIN items i ON ui.itemId = i.id
        WHERE ui.id = :inventoryId
    """)
    suspend fun getInventoryEntry(inventoryId: Long): InventoryItemEntry?
}
