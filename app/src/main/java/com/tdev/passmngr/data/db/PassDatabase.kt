package com.tdev.passmngr.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.model.PasswordHistory

@Database(
    entities = [Password::class, PasswordHistory::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class PassDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
    abstract fun historyDao(): PasswordHistoryDao

    companion object {
        @Volatile private var instance: PassDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE passwords ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE passwords ADD COLUMN lastUsedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS password_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "passwordId INTEGER NOT NULL, " +
                    "encryptedPassword TEXT NOT NULL, " +
                    "savedAt INTEGER NOT NULL)"
                )
            }
        }

        fun getInstance(context: Context): PassDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PassDatabase::class.java,
                    "pass_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
