package com.ezDrop.app

import android.app.Application
import com.ezDrop.app.data.db.AppDatabase

class EzDropApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
}
