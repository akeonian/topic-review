package com.example.topicreview.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Topic::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(SqlConverters::class)
abstract class ReviewDatabase: RoomDatabase() {

    abstract fun reviewDao(): ReviewDao

    companion object {

        private var INSTANCE: ReviewDatabase? = null

        fun getDatabase(context: Context): ReviewDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ReviewDatabase::class.java,
                    "review_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}