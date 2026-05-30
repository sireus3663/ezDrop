package com.ezDrop.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "case_items",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
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
        Index("caseId"),
        Index("itemId")
    ]
)
data class CaseItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caseId: Long,
    val itemId: Long,
    val dropWeight: Int
)
