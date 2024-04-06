package com.apextech.bexatobotuz.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.apextech.bexatobotuz.data.local.dao.CyrillDao
import com.apextech.bexatobotuz.data.local.dao.HistoryDao
import com.apextech.bexatobotuz.data.local.dao.LatinDao
import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity

@Database(entities = [LatinEntity::class, CyrillEntity::class, HistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun latinDao(): LatinDao
    abstract fun cyrillDao(): CyrillDao
    abstract fun historyDao(): HistoryDao
}