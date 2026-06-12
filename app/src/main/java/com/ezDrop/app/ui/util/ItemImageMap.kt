package com.ezDrop.app.ui.util

import com.ezDrop.app.data.skin.SkinImageRepository
import kotlinx.coroutines.flow.StateFlow

object ItemImageMap {

    val urlCache: StateFlow<Map<String, String>> = SkinImageRepository.urlCache
    val descriptionCache: StateFlow<Map<String, String>> = SkinImageRepository.descriptionCache

    fun getUrl(name: String, wearTier: String): String? {
        return SkinImageRepository.getUrlByName(name, wearTier)
    }

    fun getDescription(name: String): String? {
        return SkinImageRepository.getDescription(name)
    }
}
