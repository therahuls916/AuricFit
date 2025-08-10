package com.rahul.auric.auricfit.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stepDao(): StepDao

    companion object {
        // The @Volatile annotation ensures that the INSTANCE variable is always up-to-date
        // and visible to all execution threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's not null.
            // If it is null, create the database in a synchronized block to avoid race conditions.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auricfit_database" // This is the name of the database file
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}