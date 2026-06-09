package com.ezDrop.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ezDrop.app.data.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE nickname = :nickname LIMIT 1")
    suspend fun getByNickname(nickname: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<UserEntity?>

    @Update
    suspend fun update(user: UserEntity): Int

    @Query("UPDATE users SET balance = :balance WHERE id = :userId")
    suspend fun updateBalance(userId: Long, balance: Int): Int

    @Query("UPDATE users SET level = :level WHERE id = :userId")
    suspend fun updateLevel(userId: Long, level: Int): Int

    @Query("UPDATE users SET nickname = :nickname WHERE id = :userId")
    suspend fun updateNickname(userId: Long, nickname: String): Int

    @Query("UPDATE users SET avatarUri = :uri WHERE id = :userId")
    suspend fun updateAvatarUri(userId: Long, uri: String?): Int

    @Query("UPDATE users SET netWorth = :netWorth WHERE id = :userId")
    suspend fun updateNetWorth(userId: Long, netWorth: Int): Int
}
