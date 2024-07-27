package uk.ac.tees.mad.d3617913

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}