package com.ezDrop.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ezDrop.app.data.db.dao.CaseDao
import com.ezDrop.app.data.db.dao.CaseItemDao
import com.ezDrop.app.data.db.dao.InventoryDao
import com.ezDrop.app.data.db.dao.ItemDao
import com.ezDrop.app.data.db.dao.UserDao
import com.ezDrop.app.data.db.entity.CaseEntity
import com.ezDrop.app.data.db.entity.CaseItemEntity
import com.ezDrop.app.data.db.entity.InventoryEntity
import com.ezDrop.app.data.db.entity.ItemEntity
import com.ezDrop.app.data.db.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Database(
    entities = [UserEntity::class, ItemEntity::class, InventoryEntity::class, CaseEntity::class, CaseItemEntity::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun caseDao(): CaseDao
    abstract fun caseItemDao(): CaseItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val seeded = java.util.concurrent.atomic.AtomicBoolean(false)

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ezdrop_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                seedAsync(instance)
                instance
            }
        }

        private fun seedAsync(instance: AppDatabase) {
            if (!seeded.compareAndSet(false, true)) return
            Thread {
                runBlocking {
                    val hasItems = withContext(Dispatchers.IO) {
                        instance.openHelper.readableDatabase
                            .compileStatement("SELECT COUNT(*) FROM items")
                            .simpleQueryForLong() > 0
                    }
                    if (hasItems) return@runBlocking

                    val itemIds = instance.itemDao().insertAll(SeedData.items)
                    val caseIds = instance.caseDao().insertAll(SeedData.cases)
                    instance.caseItemDao().insertAll(SeedData.buildCaseItems(itemIds, caseIds))
                }
            }.apply { isDaemon = true }.start()
        }
    }
}
