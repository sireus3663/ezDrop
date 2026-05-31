package com.ezDrop.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val rarity: String,
    val quality: String,
    val category: String,
    val imageRes: String,
    val price: Int = 0
)
