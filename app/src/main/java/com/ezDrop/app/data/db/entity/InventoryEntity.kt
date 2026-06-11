package com.ezDrop.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_inventory",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["itemId"])
    ]
)
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val itemId: Long,
    val wearFloat: Float = 0f,
    val finalPrice: Int = 0
)
