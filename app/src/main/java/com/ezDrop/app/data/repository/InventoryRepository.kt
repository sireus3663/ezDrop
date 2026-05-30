package com.ezDrop.app.data.repository

import com.ezDrop.app.data.db.dao.InventoryDao
import com.ezDrop.app.data.db.dao.ItemDao
import com.ezDrop.app.data.db.entity.InventoryEntity
import com.ezDrop.app.data.db.entity.ItemEntity

class InventoryRepository(
    private val itemDao: ItemDao,
    private val inventoryDao: InventoryDao
) {
    suspend fun getUserInventory(userId: Long): List<ItemEntity> {
        return inventoryDao.getUserItems(userId)
    }

    suspend fun addItemToUser(userId: Long, itemId: Long): Long {
        return inventoryDao.insert(
            InventoryEntity(userId = userId, itemId = itemId)
        )
    }

    suspend fun getAllItems(): List<ItemEntity> {
        return itemDao.getAll()
    }

    suspend fun getItemById(id: Long): ItemEntity? {
        return itemDao.getById(id)
    }
}
