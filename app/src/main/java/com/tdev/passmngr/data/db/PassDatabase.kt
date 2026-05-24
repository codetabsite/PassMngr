package com.tdev.passmngr.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tdev.passmngr.data.model.Password

@Database(entities = [Password::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PassDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

    companion object {
        @Volatile private var INSTANCE: PassDatabase? = null

        fun getInstance(context: Context): PassDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PassDatabase::class.java,
                    "passmngr.db"
                ).build().also { INSTANCE = it }
            }
    }
}
