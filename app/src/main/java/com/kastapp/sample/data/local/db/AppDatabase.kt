package com.kastapp.sample.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kastapp.sample.data.model.Feed
import com.kastapp.sample.data.local.db.dao.FeedDao

@Database(
    entities = [
        Feed::class
    ],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun feedDao(): FeedDao

    override fun clearAllTables() {
        feedDao().deleteAll()
    }
}
