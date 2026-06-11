package com.ezDrop.app

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.ezDrop.app.data.db.AppDatabase
import com.ezDrop.app.data.skin.SkinImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EzDropApp : Application(), SingletonImageLoader.Factory {
    val database by lazy { AppDatabase.getInstance(this) }
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            SkinImageRepository.ensureLoaded(this@EzDropApp)
        }
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .build()
    }
}
