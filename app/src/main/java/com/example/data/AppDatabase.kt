package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.QuestionDao
import com.example.data.dao.TestResultDao
import com.example.data.model.QuestionEntity
import com.example.data.model.TestResultEntity

@Database(
    entities = [QuestionEntity::class, TestResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kerala_psc_mock_test.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
