package com.tdev.passmngr.data.db

import androidx.room.*
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.model.PasswordHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY accountName ASC")
    fun getAllByName(): Flow<List<Password>>

    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAllByDate(): Flow<List<Password>>

    @Query("SELECT * FROM passwords ORDER BY lastUsedAt DESC")
    fun getAllByLastUsed(): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getById(id: Long): Password?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: Password): Long

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)

    @Query("UPDATE passwords SET lastUsedAt = :time WHERE id = :id")
    suspend fun updateLastUsed(id: Long, time: Long)
}

@Dao
interface PasswordHistoryDao {
    @Query("SELECT * FROM password_history WHERE passwordId = :id ORDER BY savedAt DESC LIMIT 5")
    suspend fun getHistory(id: Long): List<PasswordHistory>

    @Insert
    suspend fun insert(history: PasswordHistory)

    @Query("DELETE FROM password_history WHERE passwordId = :id AND id NOT IN " +
           "(SELECT id FROM password_history WHERE passwordId = :id ORDER BY savedAt DESC LIMIT 5)")
    suspend fun pruneOldHistory(id: Long)
}
