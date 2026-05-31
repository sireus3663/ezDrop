package com.ezDrop.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["nickname"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nickname: String,
    val email: String,
    val password: String,
    val balance: Int = 0,
    val level: Int = 1,
    val avatarUri: String? = null,
    val xp: Int = 0,
    val xpNeed: Int = 100,
    val netWorth: Int = 0
)
