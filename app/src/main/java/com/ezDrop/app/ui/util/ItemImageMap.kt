package com.ezDrop.app.ui.util

import com.ezDrop.app.data.skin.SkinImageRepository
import kotlinx.coroutines.flow.StateFlow

object ItemImageMap {

    val urlCache: StateFlow<Map<String, String>> = SkinImageRepository.urlCache

    fun getUrl(name: String, wearTier: String): String? {
        return SkinImageRepository.getUrlByName(name, wearTier)
    }
}
