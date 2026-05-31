package com.ezDrop.app.data.repository

import com.ezDrop.app.data.db.dao.UserDao
import com.ezDrop.app.data.db.entity.UserEntity
import java.security.MessageDigest

class AuthRepository(private val userDao: UserDao) {

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    suspend fun register(
        nickname: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        if (userDao.getByEmail(email) != null) {
            return Result.failure(Exception("Email already registered"))
        }
        if (userDao.getByNickname(nickname) != null) {
            return Result.failure(Exception("Nickname already taken"))
        }
        val user = UserEntity(
            nickname = nickname,
            email = email,
            password = hashPassword(password)
        )
        val id = userDao.insert(user)
        return Result.success(user.copy(id = id))
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
            ?: return Result.failure(Exception("User not found"))
        if (user.password != hashPassword(password)) {
            return Result.failure(Exception("Wrong password"))
        }
        return Result.success(user)
    }

    suspend fun getUser(id: Long): UserEntity? {
        return userDao.getById(id)
    }

    suspend fun updateNickname(userId: Long, nickname: String): Boolean {
        return userDao.updateNickname(userId, nickname) > 0
    }

    suspend fun updateAvatarUri(userId: Long, uri: String?): Boolean {
        return userDao.updateAvatarUri(userId, uri) > 0
    }
}
