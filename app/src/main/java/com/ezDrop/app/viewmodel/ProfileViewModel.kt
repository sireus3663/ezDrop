package com.ezDrop.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ezDrop.app.EzDropApp
import com.ezDrop.app.SessionManager
import com.ezDrop.app.data.db.entity.UserEntity
import com.ezDrop.app.data.repository.AuthRepository
import com.ezDrop.app.data.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ProfileState(
    val user: UserEntity? = null,
    val inventoryValue: Int = 0,
    val isLoading: Boolean = true,
    val editing: Boolean = false,
    val editNickname: String = "",
    val saving: Boolean = false,
    val error: String? = null,
    val avatarRefresh: Int = 0,
    val pendingAvatarUri: Uri? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(
        (application as EzDropApp).database.userDao()
    )
    private val inventoryRepository = InventoryRepository(
        (application as EzDropApp).database.itemDao(),
        (application as EzDropApp).database.inventoryDao()
    )
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            val hasData = _state.value.user != null
            _state.value = _state.value.copy(isLoading = !hasData)
            val user = authRepository.getUser(userId)
            val inventoryValue = inventoryRepository.getUserInventory(userId).sumOf { it.basePrice }
            _state.value = _state.value.copy(
                user = user,
                inventoryValue = inventoryValue,
                isLoading = false
            )
        }
    }

    fun startEditing() {
        _state.value = _state.value.copy(
            editing = true,
            editNickname = _state.value.user?.nickname ?: ""
        )
    }

    fun stopEditing() {
        _state.value = _state.value.copy(editing = false, error = null, pendingAvatarUri = null)
    }

    fun updateEditNickname(value: String) {
        _state.value = _state.value.copy(editNickname = value)
    }

    fun selectAvatar(uri: Uri) {
        _state.value = _state.value.copy(pendingAvatarUri = uri)
    }

    fun saveChanges() {
        val userId = sessionManager.getUserId() ?: return
        val nickname = _state.value.editNickname.trim()
        val pendingUri = _state.value.pendingAvatarUri

        if (nickname.isBlank() && pendingUri == null) return
        if (nickname.isBlank() && pendingUri != null) {
            // only avatar selected, no nickname changes
        } else if (nickname.isBlank()) {
            _state.value = _state.value.copy(error = "Nickname cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, error = null)

            var savedPath: String? = null
            if (pendingUri != null) {
                savedPath = withContext(Dispatchers.IO) {
                    saveImageToInternalStorage(pendingUri, userId)
                }
                if (savedPath == null) {
                    _state.value = _state.value.copy(saving = false, error = "Failed to save avatar")
                    return@launch
                }
                authRepository.updateAvatarUri(userId, savedPath)
            }

            if (nickname.isNotBlank()) {
                val success = authRepository.updateNickname(userId, nickname)
                if (!success) {
                    _state.value = _state.value.copy(saving = false, error = "Failed to update nickname")
                    return@launch
                }
            }

            _state.value = _state.value.copy(
                user = _state.value.user?.copy(
                    nickname = if (nickname.isNotBlank()) nickname else _state.value.user!!.nickname,
                    avatarUri = savedPath ?: _state.value.user?.avatarUri
                ),
                saving = false,
                editing = false,
                pendingAvatarUri = null,
                avatarRefresh = _state.value.avatarRefresh + 1
            )
        }
    }

    fun logout() {
        sessionManager.clear()
    }

    private fun saveImageToInternalStorage(uri: Uri, userId: Long): String? {
        return try {
            val context = getApplication<Application>()
            val avatarsDir = File(context.filesDir, "avatars")
            avatarsDir.mkdirs()
            val file = File(avatarsDir, "user_${userId}.jpg")

            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap == null) return null

            val fixed = try {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd == null) {
                    bitmap
                } else {
                    val fd = pfd.fileDescriptor
                    val exif = ExifInterface(fd)
                    pfd.close()
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    rotateBitmap(bitmap, orientation)
                }
            } catch (_: Exception) {
                bitmap
            }

            val cropped = centerCropBitmap(fixed, 256)

            file.outputStream().use { out ->
                cropped.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun centerCropBitmap(source: Bitmap, targetSize: Int): Bitmap {
        val size = minOf(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val square = Bitmap.createBitmap(source, x, y, size, size)
        if (size == targetSize) return square
        return Bitmap.createScaledBitmap(square, targetSize, targetSize, true)
    }
}
