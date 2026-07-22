package com.example.electrokit.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.electrokit.data.database.dao.ComponentDao
import com.example.electrokit.data.database.entity.ComponentEntity

@Database(entities = [ComponentEntity::class], version = 1, exportSchema = false)
abstract class ElectroKitDatabase : RoomDatabase() {

    abstract fun componentDao(): ComponentDao

    companion object {
        @Volatile
        private var INSTANCE: ElectroKitDatabase? = null

        fun getDatabase(context: Context): ElectroKitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElectroKitDatabase::class.java,
                    "electrokit_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
