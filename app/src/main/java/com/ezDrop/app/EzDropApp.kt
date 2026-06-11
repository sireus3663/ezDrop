package com.ezDrop.app

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.ezDrop.app.data.db.AppDatabase

class EzDropApp : Application(), SingletonImageLoader.Factory {
    val database by lazy { AppDatabase.getInstance(this) }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .build()
    }
}
